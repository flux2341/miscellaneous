from django.db import models
from main.models import SubmissionStatus, Tag
from users.models import User
import os


# social, meeting, action
class EventType(models.Model):
    name = models.CharField(max_length=40)

    def __str__(self):
        return self.name
    
    class Meta:
        ordering = ['name']


class Event(models.Model):
    name = models.CharField(max_length=200)
    description = models.TextField(blank=True)
    start_datetime = models.DateTimeField()
    end_datetime = models.DateTimeField(null=True, blank=True)
    address = models.CharField(max_length=500)
    hosts = models.CharField(max_length=500)
    url = models.CharField(max_length=500)
    type = models.ForeignKey(EventType, on_delete=models.PROTECT, related_name='events')
    tags = models.ManyToManyField(Tag, related_name='events', blank=True)
    submission_status = models.ForeignKey(SubmissionStatus, on_delete=models.PROTECT, related_name='events')
    submitted_by = models.ForeignKey(User, on_delete=models.PROTECT, related_name='submitted_events', null=True, blank=True)
    edited_timestamp = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.submission_status.name + ' - ' + self.title

    def pretty_datetime(self):

        if os.name == 'nt':
            dtf = '%A %#m/%#d %#I:%M %p'
            tf = '%#I:%M %p'
        else:
            dtf = '%A %-m/%-d %-I:%M %p'
            tf = '%-I:%M %p'

        sdt = self.start_datetime
        # sdt = timezone.localtime(sdt, pytz.timezone('US/Pacific'))

        edt = self.end_datetime
        if edt is None:
            return sdt.strftime(dtf)
        else:
            # edt = timezone.localtime(edt, pytz.timezone('US/Pacific'))
            if sdt.year == edt.year \
                and sdt.month == edt.month \
                and sdt.day == edt.day:
                return sdt.strftime(dtf) \
                        + ' - ' + edt.strftime(tf)
            return sdt.strftime(dtf) \
                + ' - ' + edt.strftime(dtf)

    class Meta:
        ordering = ['start_datetime']

