from io import BytesIO

import segno
from django.http import Http404, HttpResponse
from django.views import View


class QRCodeDataRequired(Http404):
    default_message = "The 'data' parameter is required to generate a QR code."

    def __init__(self, message=None):
        if message is None:
            message = self.default_message
        super().__init__(message)


class GenerateQRView(View):
    def get(self, request):
        data = request.GET.get("data")
        scale = request.GET.get("scale", 10)
        border = request.GET.get("border", 4)
        error = request.GET.get("error", "M")
        micro = request.GET.get("micro", "false").lower() == "true"

        if not data:
            raise QRCodeDataRequired()

        if error not in ["L", "M", "Q", "H"]:
            error = "M"

        try:
            scale = int(scale)
        except (ValueError, TypeError):
            scale = 10

        try:
            border = int(border)
        except (ValueError, TypeError):
            border = 4

        qr = segno.make(data, error=error, micro=micro)

        buf = BytesIO()
        qr.save(buf, kind="png", scale=scale, border=border)
        buf.seek(0)

        return HttpResponse(buf.read(), content_type="image/png")
