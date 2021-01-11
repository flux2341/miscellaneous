from django.contrib import admin

from .models import SubmissionStatus, TagType, Tag, Redirector, LinkType

admin.site.site_header = "ARCDB Administration"


admin.site.register(SubmissionStatus)
admin.site.register(TagType)
admin.site.register(LinkType)
# class TagAdmin(admin.ModelAdmin):
#     list_display = ('display_name', 'type')
# admin.site.register(Tag, TagAdmin)
admin.site.register(Tag)
admin.site.register(Redirector)


