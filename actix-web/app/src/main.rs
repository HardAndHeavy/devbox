use actix_web::{get, web, App, HttpResponse, HttpServer, Responder, Result};
use serde::Deserialize;
use qrcode::QrCode;
use image::{Luma, ImageFormat};

#[get("/")]
async fn root() -> impl Responder {
    HttpResponse::Ok().body("This is the Actix Web in DevBox.")
}

#[derive(Deserialize)]
struct QrQuery {
    data: String,
    #[serde(default = "default_size")]
    size: u32,
}

fn default_size() -> u32 {
    200
}

async fn qr(query: web::Query<QrQuery>) -> Result<HttpResponse> {
    let code = QrCode::new(&query.data)
        .map_err(|e| actix_web::error::ErrorBadRequest(format!("QR generation error: {}", e)))?;
    
    let image = code.render::<Luma<u8>>()
        .dark_color(Luma([0]))
        .light_color(Luma([255]))
        .quiet_zone(false)
        .min_dimensions(query.size, query.size)
        .build();
    
    let mut buffer = Vec::new();
    let mut cursor = std::io::Cursor::new(&mut buffer);
    
    image.write_to(&mut cursor, ImageFormat::Png)
        .map_err(|e| actix_web::error::ErrorInternalServerError(format!("Image encoding error: {}", e)))?;
    
    Ok(HttpResponse::Ok()
        .content_type("image/png")
        .body(buffer))
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    HttpServer::new(|| {
        App::new()
            .service(root)
            .route("/qr", web::get().to(qr))
    })
    .bind(("0.0.0.0", 8080))?
    .run()
    .await
}
