

def validate_query_parameters(specifications, query_parameters):
    result = {}
    for specification in specifications:
        name = specification['name']
        if name in query_parameters:
            value = query_parameters[name]
            if specification['type'] == 'str':
                result[name] = value
            elif specification['type'] == 'int':
                try:
                    value = int(value)
                except ValueError:
                    return None
                if 'min' in specification and value < specification['min']:
                    return None
                if 'max' in specification and value > specification['max']:
                    return None
                result[name] = value
            elif specification['type'] == 'float':
                try:
                    value = float(value)
                except ValueError:
                    return None
                if 'min' in specification and value < specification['min']:
                    return None
                if 'max' in specification and value > specification['max']:
                    return None
                result[name] = value
        else:
            if 'default' in specification:
                result[name] = specification['default']
            else:
                result[name] = None
    return result