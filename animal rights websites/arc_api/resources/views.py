from django.shortcuts import render
from django.http import JsonResponse, Http404
from django.core.paginator import Paginator
from .models import Resource, ResourceType, ResourceLink
from main.models import Tag
from main.utilities import validate_query_parameters
                

def types(request):
    resource_types = ResourceType.objects.all()
    response = {'resource_types': []}
    for resource_type in resource_types:
        response['resource_types'].append({
            'display_name': resource_type.display_name,
            'path_name': resource_type.path_name
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
            'max': 50
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
    
    resources = Resource.objects.filter(submission_status__name='approved')

    if parameters['type'] is not None:
        type = ResourceType.objects.filter(path_name=parameters['type']).first()
        print(type)
        if type:
            resources = resources.filter(type=type)
        else:
            raise Http404('bad parameters')

    if parameters['tag'] is not None:
        tag = Tag.objects.filter(path_name=parameters['tag']).first()
        if tag:
            resources = resources.filter(tags__id=tag.id)
        else:
            raise Http404('bad parameters')
    
    
    paginator = Paginator(resources, parameters['per_page'])
    resources = paginator.page(parameters['page'])
    response = data = {'resources': [], 'pages': paginator.num_pages, 'total': paginator.count}
    for resource in resources:
        response['resources'].append({
            'name': resource.name,
            'author': resource.author,
            'description': resource.description,
            'url': resource.url,
            'type': resource.type.path_name,
            'tags': [tag.path_name for tag in resource.tags.all()],
            'links':[{'url': resource_link.url, 'type': resource_link.type.name} for resource_link in resource.links.all()]
        })
    return JsonResponse(response)


    








