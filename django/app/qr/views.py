from django.http import HttpResponse, Http404
from django.views import View
import segno
from io import BytesIO


class GenerateQRView(View):
    def get(self, request):
        data = request.GET.get('data')
        scale = request.GET.get('scale', 10)
        border = request.GET.get('border', 4)
        error = request.GET.get('error', 'M')
        micro = request.GET.get('micro', 'false').lower() == 'true'

        if not data:
            raise Http404("Параметр 'data' обязателен для генерации QR-кода.")

        if error not in ['L', 'M', 'Q', 'H']:
            error = 'M'

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
        qr.save(buf, kind='png', scale=scale, border=border)
        buf.seek(0)

        return HttpResponse(buf.read(), content_type="image/png")
