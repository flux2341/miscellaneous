

from django.urls import path
from . import views
app_name = 'resources'
urlpatterns = [
    path('', views.index, name='index'),
    path('types/', views.types, name='types')
]

