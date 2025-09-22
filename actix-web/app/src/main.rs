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

use actix_web::{get, middleware, web, App, HttpResponse, HttpServer, Responder, Result};
use image::{ImageFormat, Luma};
use qrcode::QrCode;
use serde::Deserialize;

/// Root endpoint handler that returns service information.
#[get("/")]
async fn root() -> impl Responder {
    HttpResponse::Ok().body("This is the Actix Web in DevBox.")
}

/// Query parameters for QR code generation endpoint.
#[derive(Deserialize)]
struct QrQuery {
    /// Data to encode in the QR code.
    data: String,
    /// Size of the QR code image in pixels (default: 200).
    #[serde(default = "default_size")]
    size: u32,
}

/// Returns the default size for QR code images.
const fn default_size() -> u32 {
    200
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
async fn qr(query: web::Query<QrQuery>) -> Result<HttpResponse> {
    // Validate size constraints
    if query.size < 50 || query.size > 2000 {
        return Ok(HttpResponse::BadRequest().body("Size must be between 50 and 2000 pixels"));
    }

    // Generate QR code
    let code = QrCode::new(&query.data)
        .map_err(|e| actix_web::error::ErrorBadRequest(format!("QR generation error: {e}")))?;

    // Render QR code as image
    let image = code
        .render::<Luma<u8>>()
        .dark_color(Luma([0]))
        .light_color(Luma([255]))
        .quiet_zone(false)
        .min_dimensions(query.size, query.size)
        .build();

    // Encode image to PNG
    let mut buffer = Vec::new();
    {
        let mut cursor = std::io::Cursor::new(&mut buffer);
        image.write_to(&mut cursor, ImageFormat::Png).map_err(|e| {
            actix_web::error::ErrorInternalServerError(format!("Image encoding error: {e}"))
        })?;
    }

    Ok(HttpResponse::Ok()
        .content_type("image/png")
        .append_header(("Cache-Control", "public, max-age=3600"))
        .body(buffer))
}

/// Main entry point for the QR Code Generator Service.
///
/// Starts an HTTP server on port 8080 that serves QR code generation endpoints.
#[actix_web::main]
async fn main() -> std::io::Result<()> {
    env_logger::init_from_env(env_logger::Env::new().default_filter_or("info"));

    log::info!("Starting QR Code Generator Service on 0.0.0.0:8080");

    HttpServer::new(|| {
        App::new()
            .wrap(middleware::Logger::default())
            .wrap(middleware::Compress::default())
            .service(root)
            .route("/qr", web::get().to(qr))
    })
    .bind(("0.0.0.0", 8080))?
    .run()
    .await
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_default_size() {
        assert_eq!(default_size(), 200);
    }

    #[test]
    fn test_qr_query_deserialization() {
        let json = r#"{"data": "test data", "size": 300}"#;
        let query: QrQuery = serde_json::from_str(json).unwrap();
        assert_eq!(query.data, "test data");
        assert_eq!(query.size, 300);
    }

    #[test]
    fn test_qr_query_default_size() {
        let json = r#"{"data": "test data"}"#;
        let query: QrQuery = serde_json::from_str(json).unwrap();
        assert_eq!(query.data, "test data");
        assert_eq!(query.size, 200);
    }
}
