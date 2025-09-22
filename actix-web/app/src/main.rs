//! Main entry point for the QR Code Generator Service.
//!
//! This binary starts the Actix Web server.

use app::create_app;

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    env_logger::init_from_env(env_logger::Env::new().default_filter_or("info"));

    log::info!("Starting QR Code Generator Service on 0.0.0.0:8080");

    actix_web::HttpServer::new(create_app)
        .bind(("0.0.0.0", 8080))?
        .run()
        .await
}
