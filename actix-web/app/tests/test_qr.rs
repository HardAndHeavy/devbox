//! Integration tests for QR code generator service.

use actix_web::{http::header, test as actix_test};
use app::{create_app, default_size, QrQuery};

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

    #[actix_web::test]
    async fn test_missing_data_parameter_raises_error() {
        let app = actix_test::init_service(create_app()).await;
        let req = actix_test::TestRequest::get().uri("/qr").to_request();
        let resp = actix_test::call_service(&app, req).await;

        assert_eq!(resp.status(), 400);
        let body = actix_test::read_body(resp).await;
        let body_str = String::from_utf8_lossy(&body);
        assert!(body_str.contains("missing field `data`"));
    }

    #[actix_web::test]
    async fn test_successful_qr_generation_with_default_params() {
        let app = actix_test::init_service(create_app()).await;
        let req = actix_test::TestRequest::get()
            .uri("/qr?data=https://example.com")
            .to_request();
        let resp = actix_test::call_service(&app, req).await;

        assert!(resp.status().is_success());
        assert_eq!(
            resp.headers().get(header::CONTENT_TYPE).unwrap(),
            "image/png"
        );
    }
}
