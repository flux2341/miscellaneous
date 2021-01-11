from django.db import models
from main.models import SubmissionStatus, Tag, LinkType
from users.models import User

# app, book, documentary film, fictional film
class ResourceType(models.Model):
    display_name = models.CharField(max_length=40)
    path_name = models.CharField(max_length=40)

    def __str__(self):
        return self.display_name
    
    class Meta:
        ordering = ['display_name']


class Resource(models.Model):
    name = models.CharField(max_length=200)
    author = models.CharField(max_length=200, blank=True)
    description = models.TextField(blank=True)
    url = models.CharField(max_length=200)
    type = models.ForeignKey(ResourceType, on_delete=models.PROTECT, related_name='resources')
    tags = models.ManyToManyField(Tag, related_name='resources')
    submission_status = models.ForeignKey(SubmissionStatus, on_delete=models.PROTECT, related_name="resources")
    submitted_by = models.ForeignKey(User, on_delete=models.PROTECT, related_name='submitted_resources', null=True, blank=True)
    edited_timestamp = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.name

    class Meta:
        ordering = ['name']


class ResourceLink(models.Model):
    resource = models.ForeignKey(Resource, on_delete=models.PROTECT, related_name="links")
    type = models.ForeignKey(LinkType, on_delete=models.PROTECT, related_name="resource_links")
    url = models.CharField(max_length=200)
    order = models.IntegerField()
    
    def __str__(self):
        return self.resource.name + ' - ' + self.type.name + ' - ' + self.url
    
    class Meta:
        ordering = ['resource__name', 'order']
    