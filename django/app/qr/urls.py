from django.urls import path

from . import views

urlpatterns = [
    path("", views.GenerateQRView.as_view(), name="generate_qr"),
]
