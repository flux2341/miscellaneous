from django.contrib import admin


from .models import Resource, ResourceType, ResourceLink



class ResourceAdmin(admin.ModelAdmin):
    list_display = ('name', 'type', 'submission_status')
    list_filter = ('type', 'submission_status')
admin.site.register(Resource, ResourceAdmin)

admin.site.register(ResourceType)

class ResourceLinkAdmin(admin.ModelAdmin):
    list_display = ('order', 'resource', 'type', 'url')
admin.site.register(ResourceLink, ResourceLinkAdmin)

