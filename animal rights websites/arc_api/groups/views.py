from django.shortcuts import render
from django.http import JsonResponse, Http404
from django.core.paginator import Paginator
from .models import Group, GroupType, GroupLink, GroupScope
from main.models import Tag
from main.utilities import validate_query_parameters


def types(request):
    group_types = GroupType.objects.all()
    response = {'group_types': [], 'group_scopes': []}
    for group_type in group_types:
        response['group_types'].append({
            'name': group_type.name
        })
    group_scopes = GroupScope.objects.all()
    for group_scope in group_scopes:
        response['group_scopes'].append({
            'name': group_scope.name
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
            'name': 'scope',
            'type': 'str',
            'default': None
        },{
            'name': 'tag',
            'type': 'str',
            'default': None,
        }], request.GET)
    if parameters is None:
        raise Http404('bad parameters')
    
    groups = Group.objects.filter(submission_status__name='approved')

    if parameters['type'] is not None:
        type = GroupType.objects.filter(name=parameters['type']).first()
        if type:
            groups = groups.filter(type=type)
        else:
            raise Http404('bad parameters')

    if parameters['scope'] is not None:
        scope = GroupScope.objects.filter(name=parameters['scope']).first()
        if scope:
            groups = groups.filter(scope=scope)
        else:
            raise Http404('bad parameters')
    
    if parameters['tag'] is not None:
        tag = Tag.objects.filter(path_name=parameters['tag']).first()
        if tag:
            groups = groups.filter(tags__id=tag.id)
        else:
            raise Http404('bad parameters')
    
    
    paginator = Paginator(groups, parameters['per_page'])
    groups = paginator.page(parameters['page'])
    response = data = {'groups': [], 'pages': paginator.num_pages, 'total': paginator.count}
    for group in groups:
        response['groups'].append({
            'name': group.name,
            'description': group.description,
            'type': group.type.name,
            'scope': group.scope.name,
            'url': group.url,
            'email': group.email,
            'address': group.address,
            'tags': [tag.path_name for tag in group.tags.all()],
            'links':[{'url': group_link.url, 'type': group_link.type.name} for group_link in group.links.all()]
        })
    return JsonResponse(response)


    








