from io import BytesIO
from unittest.mock import Mock, patch

import pytest
import segno
from fastapi import status
from fastapi.testclient import TestClient

from main import app

DEFAULT_SCALE = 10
DEFAULT_BORDER = 4
CUSTOM_SCALE = 5
CUSTOM_BORDER = 2


@pytest.fixture
def client():
    return TestClient(app)


class TestGenerateQR:
    def test_missing_data_parameter_raises_error(self, client):
        """Should return 422 error if data parameter is missing"""
        response = client.get("/qr")

        assert response.status_code == status.HTTP_422_UNPROCESSABLE_ENTITY
        assert "field required" in response.text.lower()

    def test_successful_qr_generation_with_default_params(self, client):
        """Should successfully generate QR code with default parameters"""
        test_data = "https://example.com"
        response = client.get("/qr", params={"data": test_data})

        assert response.status_code == status.HTTP_200_OK
        assert response.headers["content-type"] == "image/png"
        assert len(response.content) > 0
        assert response.content.startswith(b"\x89PNG")  # PNG signature

    def test_successful_qr_generation_with_custom_params(self, client):
        """Should successfully generate QR code with custom parameters"""
        test_data = "test-data"

        with patch("main.segno.make") as mock_make:
            mock_qr = Mock()
            mock_make.return_value = mock_qr

            response = client.get(
                "/qr",
                params={
                    "data": test_data,
                    "scale": CUSTOM_SCALE,
                    "border": CUSTOM_BORDER,
                    "error": "H",
                    "micro": True,
                },
            )

            assert response.status_code == status.HTTP_200_OK

            mock_make.assert_called_once_with(test_data, error="H", micro=True)
            mock_qr.save.assert_called_once()

            call_args = mock_qr.save.call_args
            assert call_args[1]["scale"] == CUSTOM_SCALE
            assert call_args[1]["border"] == CUSTOM_BORDER
            assert call_args[1]["kind"] == "png"

    def test_invalid_scale_parameter_validation(self, client):
        """Should return validation error for invalid scale parameter"""
        response = client.get("/qr", params={"data": "test", "scale": "invalid"})

        assert response.status_code == status.HTTP_422_UNPROCESSABLE_ENTITY
        assert "int_parsing" in response.text.lower()
        assert "scale" in response.text.lower()

    def test_invalid_border_parameter_validation(self, client):
        """Should return validation error for invalid border parameter"""
        response = client.get("/qr", params={"data": "test", "border": "invalid"})

        assert response.status_code == status.HTTP_422_UNPROCESSABLE_ENTITY
        assert "int_parsing" in response.text.lower()
        assert "border" in response.text.lower()

    def test_invalid_error_parameter_validation(self, client):
        """Should return validation error for invalid error correction level"""
        response = client.get("/qr", params={"data": "test", "error": "INVALID"})

        assert response.status_code == status.HTTP_422_UNPROCESSABLE_ENTITY
        assert "error" in response.text.lower()
        assert "input should be" in response.text.lower()
        assert "l', 'm', 'q' or 'h'" in response.text.lower()

    @pytest.mark.parametrize(
        ("micro_value", "expected_status"),
        [
            ("true", status.HTTP_200_OK),
            ("false", status.HTTP_200_OK),
            ("True", status.HTTP_200_OK),
            ("False", status.HTTP_200_OK),
            ("1", status.HTTP_200_OK),
            ("0", status.HTTP_200_OK),
            ("invalid", status.HTTP_422_UNPROCESSABLE_ENTITY),  # Невалидный boolean
        ],
    )
    def test_micro_parameter_parsing(self, client, micro_value, expected_status):
        """Should correctly handle micro parameter variations"""
        response = client.get("/qr", params={"data": "test", "micro": micro_value})

        assert response.status_code == expected_status

    def test_identical_data_generates_identical_qr(self, client):
        """Should generate identical QR codes for identical data"""
        test_data = "consistent data"

        response1 = client.get("/qr", params={"data": test_data})
        response2 = client.get("/qr", params={"data": test_data})

        assert response1.status_code == status.HTTP_200_OK
        assert response2.status_code == status.HTTP_200_OK
        assert response1.content == response2.content

    def test_different_data_generates_different_qr(self, client):
        """Should generate different QR codes for different data"""
        response1 = client.get("/qr", params={"data": "data1"})
        response2 = client.get("/qr", params={"data": "data2"})

        assert response1.status_code == status.HTTP_200_OK
        assert response2.status_code == status.HTTP_200_OK
        assert response1.content != response2.content

    def test_qr_generation_with_special_characters(self, client):
        """Should handle special characters in data"""
        special_data = "Test with special chars: !@#$%^&*()_+={[}]|\\:;\"'<,>.?/"
        response = client.get("/qr", params={"data": special_data})

        assert response.status_code == status.HTTP_200_OK
        assert len(response.content) > 0

    def test_qr_generation_with_unicode(self, client):
        """Should handle unicode characters in data"""
        unicode_data = "Test with unicode: Hello World 🎉"
        response = client.get("/qr", params={"data": unicode_data})

        assert response.status_code == status.HTTP_200_OK
        assert len(response.content) > 0

    def test_integration_qr_content_validity(self, client):
        """Integration test: Generated QR code should be valid and decodable"""
        test_data = "Test QR Data"
        response = client.get("/qr", params={"data": test_data})

        assert response.status_code == status.HTTP_200_OK

        qr = segno.make(test_data, error="M", micro=False)
        buf = BytesIO()
        qr.save(buf, kind="png", scale=DEFAULT_SCALE, border=DEFAULT_BORDER)
        expected_content = buf.getvalue()

        assert response.content == expected_content


class TestRootEndpoint:
    def test_root_endpoint(self, client):
        """Should return welcome message on root endpoint"""
        response = client.get("/")

        assert response.status_code == status.HTTP_200_OK
        assert "FastAPI in DevBox" in response.text
