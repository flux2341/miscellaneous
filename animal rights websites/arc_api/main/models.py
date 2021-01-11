from django.db import models
from users.models import User


class SubmissionStatus(models.Model):
    name = models.CharField(max_length=40)
    
    def __str__(self):
        return self.name
    
    class Meta:
        ordering = ['name']

class TagType(models.Model):
    name = models.CharField(max_length=40)

    def __str__(self):
        return self.name
    
    class Meta:
        ordering = ['name']

class Tag(models.Model):
    path_name = models.CharField(max_length=40)
    display_name = models.CharField(max_length=40)
    type = models.ForeignKey(TagType, on_delete=models.PROTECT, related_name='tags')

    def __str__(self):
        return self.type.name + ': ' + self.display_name

    class Meta:
        ordering = ['type__name', 'display_name']

# anrico.org/r/<code>
class Redirector(models.Model):
    name = models.CharField(max_length=200)
    code = models.CharField(max_length=20)
    count = models.IntegerField()
    created_timestamp = models.DateTimeField(auto_now_add=True)
    edited_timestamp = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.name

    class Meta:
        ordering = ['name']



class LinkType(models.Model):
    name = models.CharField(max_length=40)

    def __str__(self):
        return self.name
    
    class Meta:
        ordering = ['name']


