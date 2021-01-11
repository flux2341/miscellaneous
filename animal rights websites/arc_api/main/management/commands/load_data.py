from django.core.management.base import BaseCommand

from main.models import SubmissionStatus, LinkType
from events.models import Event, EventType
from groups.models import Group, GroupType, GroupScope, GroupLink
from resources.models import Resource, ResourceType, ResourceLink
import json

def load_json(path):
    with open(path, 'r', encoding='utf-8') as file:
        text = file.read()
    return json.loads(text)


def get_link_type_from_url(url):
    link_type_names = ['facebook', 'instagram', 'twitter', 'youtube', 'meetup']
    for name in link_type_names:
        if name in url:
            link_type_name = name
            break
    else:
        link_type_name = 'website'
    link_type, created = LinkType.objects.get_or_create(name=link_type_name)
    return link_type


class Command(BaseCommand):
    def handle(self, *args, **options):

        submission_approved, created = SubmissionStatus.objects.get_or_create(name='approved')

        # path_events = './temp_data/events.json'
        # events = load_json(path_events)['events']
        # event_type_other, created = EventType.objects.get_or_create(name='other')
        # Event.objects.all().delete()
        # for event_data in events:
        #     event_data['submission_status'] = submission_approved
        #     event_data['submitted_by'] = None
        #     event_data['type'] = event_type_other
        #     if event_data['end_datetime'] == '':
        #         event_data['end_datetime'] = None

        #     # print(event_data)

        #     event = Event(**event_data)
        #     event.save()
        

        # path_groups = './temp_data/groups.json'
        # groups = load_json(path_groups)['groups']
        # group_scope, created = GroupScope.objects.get_or_create(name='local')
        # GroupLink.objects.all().delete()
        # Group.objects.all().delete()
        # for group_data in groups:
        #     urls = group_data['urls']
        #     del group_data['urls']
        #     del group_data['status']
        #     group_data['type'], created = GroupType.objects.get_or_create(name=group_data['type'])
        #     group_data['scope'] = group_scope
        #     group_data['submission_status'] = submission_approved
        #     group_data['url'] = urls[0]
        #     group = Group(**group_data)
        #     group.save()

        #     for i, url in enumerate(urls):
        #         link_type = get_link_type_from_url(url)
        #         group_link = GroupLink(group=group, type=link_type, url=url, order=i)
        #         group_link.save()
            

        

        path_resources = './temp_data/resources.json'
        resources = load_json(path_resources)['resources']
        ResourceLink.objects.all().delete()
        Resource.objects.all().delete()
        for resource_data in resources:
            resource_data['type'], created = ResourceType.objects.get_or_create(name=resource_data['type'])
            resource_data['submission_status'] = submission_approved
            urls = resource_data['urls']
            del resource_data['urls']
            if 'email' in resource_data:
                del resource_data['email']

            resource = Resource(**resource_data)
            resource.save()

            for i, url in enumerate(urls):
                link_type = get_link_type_from_url(url)
                resource_link = ResourceLink(resource=resource, type=link_type, url=url, order=i)
                resource_link.save()

