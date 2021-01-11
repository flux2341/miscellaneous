from django.contrib import admin

from .models import Event, EventType

class EventAdmin(admin.ModelAdmin):
    list_display = ('name', 'type', 'start_datetime', 'url', 'submission_status')
    list_filter = ('type', 'submission_status')

admin.site.register(Event, EventAdmin)
admin.site.register(EventType)

