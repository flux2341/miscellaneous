from django.db import models
from main.models import LinkType, SubmissionStatus, Tag
from users.models import User


# community, organization, sanctuary
class GroupType(models.Model):
    name = models.CharField(max_length=50)
    
    def __str__(self):
        return self.name

    class Meta:
        ordering = ['name']


# local, regional, national, international, online
class GroupScope(models.Model):
    name = models.CharField(max_length=50)
    
    def __str__(self):
        return self.name

    class Meta:
        ordering = ['name']


class Group(models.Model):
    name = models.CharField(max_length=200)
    description = models.CharField(max_length=500, blank=True)
    type = models.ForeignKey(GroupType, on_delete=models.PROTECT, related_name='groups')
    scope = models.ForeignKey(GroupScope, on_delete=models.PROTECT, related_name='groups')
    url = models.CharField(max_length=200, blank=True)
    email = models.CharField(max_length=50, blank=True)
    phone = models.CharField(max_length=50, blank=True)
    address = models.CharField(blank=True, max_length=200)
    tags = models.ManyToManyField(Tag, related_name='groups', blank=True)
    submission_status = models.ForeignKey(SubmissionStatus, on_delete=models.PROTECT, related_name="groups")
    submitted_by = models.ForeignKey(User, on_delete=models.PROTECT, related_name='submitted_groups', null=True, blank=True)
    edited_timestamp = models.DateTimeField(auto_now=True)
    
    def __str__(self):
        return self.name
    
    class Meta:
        ordering = ['name']


class GroupLink(models.Model):
    group = models.ForeignKey(Group, on_delete=models.PROTECT, related_name="links")
    type = models.ForeignKey(LinkType, on_delete=models.PROTECT, related_name="group_links")
    url = models.CharField(max_length=200)
    order = models.IntegerField()
    
    def __str__(self):
        return self.group.name + ' - ' + self.type.name + ' - ' + self.url
    
    class Meta:
        ordering = ['order']
    