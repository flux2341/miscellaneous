from django.shortcuts import render
from django.http import JsonResponse, Http404
from django.core.paginator import Paginator
from .models import Event, EventType
from main.models import Tag
from main.utilities import validate_query_parameters
from django.utils import timezone



def types(request):
    event_types = EventType.objects.all()
    response = {'event_types': []}
    for event_type in event_types:
        response['event_types'].append({
            'name': event_type.name
        })
    return JsonResponse(response)

def index(request):
    parameters = validate_query_parameters([{
            'name': 'page',
            'type': 'int',
            'default': 1,
            'min': 1
        },{
            'name': 'per_page',
            'type': 'int',
            'default': 20,
            'min': 0,
            'max': 100
        },{
            'name': 'type',
            'type': 'str',
            'default': None,
        },{
            'name': 'tag',
            'type': 'str',
            'default': None,
        }], request.GET)
    if parameters is None:
        raise Http404('bad parameters')
    
    events = Event.objects.filter(submission_status__name='approved', start_datetime__gte=timezone.now())

    if parameters['type'] is not None:
        type = EventType.objects.filter(name=parameters['type']).first()
        if type:
            events = events.filter(type=type)
        else:
            raise Http404('bad parameters')
    
    if parameters['tag'] is not None:
        tag = Tag.objects.filter(path_name=parameters['tag']).first()
        if tag:
            events = events.filter(tags__id=tag.id)
        else:
            raise Http404('bad parameters')
    
    
    paginator = Paginator(events, parameters['per_page'])
    events = paginator.page(parameters['page'])
    response = data = {'events': [], 'pages': paginator.num_pages, 'total': paginator.count}
    for event in events:
        response['events'].append({
            'name': event.name,
            'description': event.description,
            'display_datetime': event.pretty_datetime(),
            'start_datetime': event.start_datetime.isoformat(),
            'end_datetime': None if event.end_datetime is None else event.end_datetime.isoformat(),
            'address': event.address,
            'hosts': event.hosts,
            'url': event.url,
            'type': event.type.name,
            'tags': [tag.path_name for tag in event.tags.all()]
        })
    return JsonResponse(response)


    
