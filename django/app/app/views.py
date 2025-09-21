from django.http import HttpResponse


def home_view(_request):
    return HttpResponse("This is the Django in DevBox.")
