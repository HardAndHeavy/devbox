from django.http import HttpResponse

def home_view(request):
    return HttpResponse("This is the Django in DevBox.")
