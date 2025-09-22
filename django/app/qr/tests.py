from io import BytesIO
from unittest.mock import Mock, patch

import pytest
import segno
from django.http import Http404
from django.test import RequestFactory

from .views import GenerateQRView, QRCodeDataRequired

HTTP_OK = 200
DEFAULT_SCALE = 10
DEFAULT_BORDER = 4
CUSTOM_SCALE = 5
CUSTOM_BORDER = 2


@pytest.fixture
def factory():
    return RequestFactory()


@pytest.fixture
def view():
    return GenerateQRView.as_view()


@pytest.mark.django_db
class TestGenerateQRView:
    def test_missing_data_parameter_raises_404(self, factory, view):
        """Should raise QRCodeDataRequired (404) error if data parameter is missing"""
        request = factory.get("/qr/")

        with pytest.raises(QRCodeDataRequired) as exc_info:
            view(request)

        assert str(exc_info.value) == "The 'data' parameter is required to generate a QR code."

    def test_successful_qr_generation_with_default_params(self, factory, view):
        """Should successfully generate QR code with default parameters"""
        test_data = "https://example.com"
        request = factory.get("/qr/", {"data": test_data})

        response = view(request)

        assert response.status_code == HTTP_OK
        assert response["Content-Type"] == "image/png"
        assert len(response.content) > 0
        assert response.content.startswith(b"\x89PNG")  # PNG signature

    def test_successful_qr_generation_with_custom_params(self, factory, view):
        """Should successfully generate QR code with custom parameters"""
        test_data = "test-data"
        request = factory.get(
            "/qr/",
            {
                "data": test_data,
                "scale": str(CUSTOM_SCALE),
                "border": str(CUSTOM_BORDER),
                "error": "H",
                "micro": "true",
            },
        )

        with patch("qr.views.segno.make") as mock_make:
            mock_qr = Mock()
            mock_make.return_value = mock_qr

            view(request)

            mock_make.assert_called_once_with(test_data, error="H", micro=True)
            mock_qr.save.assert_called_once()

            call_args = mock_qr.save.call_args
            assert call_args[1]["scale"] == CUSTOM_SCALE
            assert call_args[1]["border"] == CUSTOM_BORDER
            assert call_args[1]["kind"] == "png"

    def test_invalid_scale_parameter_uses_default(self, factory, view):
        """Should use default scale value when invalid scale is provided"""
        request = factory.get("/qr/", {"data": "test", "scale": "invalid"})

        with patch("qr.views.segno.make") as mock_make:
            mock_qr = Mock()
            mock_make.return_value = mock_qr

            view(request)

            call_args = mock_qr.save.call_args
            assert call_args[1]["scale"] == DEFAULT_SCALE

    def test_invalid_border_parameter_uses_default(self, factory, view):
        """Should use default border value when invalid border is provided"""
        request = factory.get("/qr/", {"data": "test", "border": "invalid"})

        with patch("qr.views.segno.make") as mock_make:
            mock_qr = Mock()
            mock_make.return_value = mock_qr

            view(request)

            call_args = mock_qr.save.call_args
            assert call_args[1]["border"] == DEFAULT_BORDER

    def test_invalid_error_parameter_uses_default(self, factory, view):
        """Should use default error correction level when invalid value is provided"""
        request = factory.get("/qr/", {"data": "test", "error": "INVALID"})

        with patch("qr.views.segno.make") as mock_make:
            mock_qr = Mock()
            mock_make.return_value = mock_qr

            view(request)

            mock_make.assert_called_once_with("test", error="M", micro=False)

    @pytest.mark.parametrize(
        ("micro_value", "expected"),
        [
            ("true", True),
            ("True", True),
            ("TRUE", True),
            ("false", False),
            ("False", False),
            ("anything", False),
            ("", False),
        ],
    )
    def test_micro_parameter_parsing(self, factory, view, micro_value, expected):
        """Should correctly parse micro parameter"""
        request = factory.get("/qr/", {"data": "test", "micro": micro_value})

        with patch("qr.views.segno.make") as mock_make:
            mock_qr = Mock()
            mock_make.return_value = mock_qr

            view(request)

            mock_make.assert_called_once_with("test", error="M", micro=expected)

    def test_identical_data_generates_identical_qr(self, factory, view):
        """Should generate identical QR codes for identical data"""
        test_data = "consistent data"

        request1 = factory.get("/qr/", {"data": test_data})
        request2 = factory.get("/qr/", {"data": test_data})

        response1 = view(request1)
        response2 = view(request2)

        assert response1.content == response2.content

    def test_different_data_generates_different_qr(self, factory, view):
        """Should generate different QR codes for different data"""
        request1 = factory.get("/qr/", {"data": "data1"})
        request2 = factory.get("/qr/", {"data": "data2"})

        response1 = view(request1)
        response2 = view(request2)

        assert response1.content != response2.content

    def test_qr_generation_with_special_characters(self, factory, view):
        """Should handle special characters in data"""
        special_data = "Test with special chars: !@#$%^&*()_+={[}]|\\:;\"'<,>.?/"
        request = factory.get("/qr/", {"data": special_data})

        response = view(request)

        assert response.status_code == HTTP_OK
        assert len(response.content) > 0

    def test_qr_generation_with_unicode(self, factory, view):
        """Should handle unicode characters in data"""
        unicode_data = "Test with unicode: Hello World 🎉"
        request = factory.get("/qr/", {"data": unicode_data})

        response = view(request)

        assert response.status_code == HTTP_OK
        assert len(response.content) > 0

    def test_integration_qr_content_validity(self, factory, view):
        """Integration test: Generated QR code should be valid and decodable"""
        test_data = "Test QR Data"
        request = factory.get("/qr/", {"data": test_data})

        response = view(request)

        qr = segno.make(test_data, error="M", micro=False)
        buf = BytesIO()
        qr.save(buf, kind="png", scale=DEFAULT_SCALE, border=DEFAULT_BORDER)
        expected_content = buf.getvalue()

        assert response.content == expected_content


@pytest.mark.django_db
class TestQRCodeDataRequired:
    def test_default_message(self):
        """Should use default message when none is provided"""
        exception = QRCodeDataRequired()
        assert str(exception) == "The 'data' parameter is required to generate a QR code."

    def test_custom_message(self):
        """Should use custom message when provided"""
        custom_message = "Custom error message"
        exception = QRCodeDataRequired(custom_message)
        assert str(exception) == custom_message

    def test_inherits_from_http404(self):
        """Should inherit from Http404"""
        exception = QRCodeDataRequired()
        assert isinstance(exception, Http404)
