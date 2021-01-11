from django.shortcuts import render
from django.http import HttpResponse

def index(request):
    routes = [{
        'path': '/resources/',
        'description': 'a list of resources',
        'parameters': [{
            'name': 'page',
            'type': 'int',
            'description': 'the page of results',
        },{
            'name': 'per_page',
            'type': 'int',
            'description': 'the number of resources per page'
        },{
            'name': 'type',
            'type': 'str',
            'description': 'the type of resources, the path_name retrieved from /resources/types/'
        },{
            'name': 'tag',
            'type': 'str',
            'description': 'the tag of a resource, the path_name retrieved from /tags/'
        }]
    }]
    # routes = [{
    #         'path': '/resources/',
    #         'description': 'a list of resources',
    #         'parameters': [{
    #             'name': 'page',
    #             'description': 'which page to look at'
    #         },{
    #             'name': 'per_page',
    #             'description': ''
    #         }]
    #     },{
    #     'path': '/api/events/',
    #     'parameters': [{
    #         'name': 'start_datetime',
    #         'required': True
    #         'description': 'the start date and time of the event',
    #         'example': ''
    #     },{
    #         'name': 'end_datetime',
    #         'required': False
    #         'description': 'the end date and time of the event',
    #         'example': ''
    #     },{
    #         'name': 'tags',
    #         'required': False
    #         'description': 'a collection of ',
    #         'example': ''
    #     }]
    # },{
    #     'path': '/api/sanctuaries/'
    # }]
    return render(request, 'main/index.html', {'routes': routes})
