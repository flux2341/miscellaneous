from django.contrib import admin

from .models import Group, GroupType, GroupLink, GroupScope


class GroupAdmin(admin.ModelAdmin):
    list_display = ('name', 'type', 'submission_status')
    list_filter = ('type', 'submission_status')
admin.site.register(Group, GroupAdmin)
admin.site.register(GroupType)
class GroupLinkAdmin(admin.ModelAdmin):
    list_display = ('order', 'group', 'type', 'url')
admin.site.register(GroupLink, GroupLinkAdmin)
admin.site.register(GroupScope)


