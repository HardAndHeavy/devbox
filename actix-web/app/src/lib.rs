//! QR Code Generator Service
//!
//! A simple web service built with Actix Web that generates QR codes from provided data.
//!
//! # Endpoints
//!
//! - `GET /` - Health check endpoint
//! - `GET /qr?data=<text>&size=<pixels>` - Generate QR code
//!
//! # Example
//!
//! ```bash
//! curl "http://localhost:8080/qr?data=Hello%20World&size=300" --output qr.png
//! ```

#![doc(html_root_url = "https://docs.rs/app/0.1.0")]
#![warn(missing_docs)]
#![warn(rustdoc::missing_crate_level_docs)]

use actix_web::body::MessageBody;
use actix_web::dev::{ServiceFactory, ServiceRequest, ServiceResponse};
use actix_web::{get, middleware, web, App, HttpResponse, Responder, Result};
use image::{ImageFormat, Luma};
use qrcode::QrCode;
use std::io::Cursor;

pub use self::defaults::default_size;
pub use self::handlers::{qr, root};
pub use self::query::QrQuery;

/// Creates and configures the Actix Web application.
///
/// This function sets up the app with middleware, routes, and services.
///
/// # Returns
///
/// Configured Actix Web `App` instance ready to be run by a server.
pub fn create_app() -> App<
    impl ServiceFactory<
        ServiceRequest,
        Response = ServiceResponse<impl MessageBody + 'static>,
        Error = actix_web::Error,
        Config = (),
        InitError = (),
    >,
> {
    App::new()
        .wrap(middleware::Logger::default())
        .wrap(middleware::Compress::default())
        .service(root)
        .route("/qr", web::get().to(qr))
}

mod handlers {
    use super::{
        get, web, Cursor, HttpResponse, ImageFormat, Luma, QrCode, QrQuery, Responder, Result,
    };

    /// Root endpoint handler that returns service information.
    #[get("/")]
    pub async fn root() -> impl Responder {
        HttpResponse::Ok().body("This is the Actix Web in DevBox.")
    }

    /// Generate QR code endpoint handler.
    ///
    /// Accepts query parameters to generate a QR code image.
    ///
    /// # Parameters
    ///
    /// * `data` - The text or URL to encode in the QR code
    /// * `size` - The size of the generated image in pixels (50-2000)
    ///
    /// # Returns
    ///
    /// PNG image with the generated QR code.
    ///
    /// # Errors
    ///
    /// Returns an error if:
    /// - QR code generation fails (e.g., invalid data)
    /// - Image encoding fails
    /// - Size is out of valid range (50-2000 pixels)
    ///
    /// # Example
    ///
    /// ```text
    /// GET /qr?data=https://example.com&size=400
    /// ```
    pub async fn qr(query: web::Query<QrQuery>) -> Result<HttpResponse> {
        if query.size < 50 || query.size > 2000 {
            return Ok(HttpResponse::BadRequest().body("Size must be between 50 and 2000 pixels"));
        }

        let code = QrCode::new(&query.data)
            .map_err(|e| actix_web::error::ErrorBadRequest(format!("QR generation error: {e}")))?;

        let image = code
            .render::<Luma<u8>>()
            .dark_color(Luma([0]))
            .light_color(Luma([255]))
            .quiet_zone(false)
            .min_dimensions(query.size, query.size)
            .build();

        let mut buffer = Vec::new();
        {
            let mut cursor = Cursor::new(&mut buffer);
            image.write_to(&mut cursor, ImageFormat::Png).map_err(|e| {
                actix_web::error::ErrorInternalServerError(format!("Image encoding error: {e}"))
            })?;
        }

        Ok(HttpResponse::Ok()
            .content_type("image/png")
            .append_header(("Cache-Control", "public, max-age=3600"))
            .body(buffer))
    }
}

mod query {
    use serde::Deserialize;

    /// Query parameters for QR code generation endpoint.
    #[derive(Deserialize)]
    pub struct QrQuery {
        /// Data to encode in the QR code.
        pub data: String,
        /// Size of the QR code image in pixels (default: 200).
        #[serde(default = "crate::default_size")]
        pub size: u32,
    }
}

mod defaults {
    /// Returns the default size for QR code images.
    #[must_use]
    pub const fn default_size() -> u32 {
        200
    }
}
