




from selenium.webdriver.firefox.firefox_binary import FirefoxBinary
# from pyvirtualdisplay import Display
from selenium import webdriver
from bs4 import BeautifulSoup
import requests
import sys
import datetime
import pyperclip
import re
import urllib
import config
import json

# Facebook automatically generates referral links for event descriptions
# this function is used by parse_facebook_event
# https://stackoverflow.com/questions/12597370/python-replace-string-pattern-with-output-of-function

def process_fb_link_replacement(match):
    match = match.group(1)
    return urllib.parse.unquote(match)


# text = '<a target="_blank" href="https://l.facebook.com/l.php?u=http%3A%2F%2Fwww.oddmaninn.org%2F%3Ffbclid%3DIwAR2brUnhUHDEDl-fWvGGQnAWPKhdLXkagHow6petdZyvDeYhfpsAmx_nG4o&amp;h=AT3ojMHr_eVzA2X_gteIJZgA5LY4qQzwlvh2lY_OJO01lmyjSV8Xm6KAstqGALTgXPOTcuF8tHKR7K31yaw3hPpgBhlcolUkY-e3IHXGGZWFoO-Or9c_cVzkjk7A1P-oEA0vILiJyCB0z2JtK5Uk" rel="nofollow noopener" data-lynx-mode="hover">www.oddmaninn.org.</a>'
# text = re.sub(r'https:\/\/l.facebook\.com\/l.php\?u=(.+)%3Ffbclid[\w%\-&;=]+', process_fb_link_replacement, text)
# print(text)
# exit()

def parse_facebook_event(url, driver):
    driver.get(url)

    soup = BeautifulSoup(driver.page_source, 'html.parser')

    # title
    title = soup.html.head.title.string
    if title == 'Facebook': # event was private or deleted or something
        return None

    # description
    # description = soup.find('div', {'data-testid': 'event-permalink-details'})
    description = soup.find('div', {'class': '_63ew'})
    if description is None:
        description = ''
    else:
        # description = description.find('span').decode_contents()
        # description = re.sub(r'https:\/\/l.facebook\.com\/l.php\?u=(.+)%3Ffbclid[\w%\-&;=]+', process_fb_link_replacement, description)
        description = description.find('span').decode_contents()
        description = re.sub(r'<a[^>]*>', '', description, flags=re.MULTILINE)
        description = re.sub(r'<\/a>', '', description, flags=re.MULTILINE)

    # start datetime
    # this might be done better, there's an li higher up with id=event_time_info
    # this seems to be the only element with a content attribute
    datetimes = soup.find('div', {'class': ['_2ycp', '_5xhk']})
    datetimes = datetimes['content']
    start_datetime = datetimes[:19]
    end_datetime = datetimes[29:48]
    start_datetime = start_datetime.replace('T', ' ')
    end_datetime = end_datetime.replace('T', ' ')

    # format = "%Y-%m-%dT%H:%M:%S"
    # start_datetime = datetime.datetime.strptime(start_datetime, format)
    # if end_datetime == '':
    #     end_datetime = ''#None
    # else:
    #     end_datetime = datetime.datetime.strptime(end_datetime, format)

    address_patterns = (('a', {'id': 'u_0_n'}),
                         ('a', {'id': 'u_0_18'}),
                         ('a', {'id': 'u_0_19'}),
                         ('a', {'id': 'u_0_1i'}),
                         ('a', {'id': 'u_0_w'}),
                         ('a', {'id': 'u_0_r'}),
                         ('span', {'class': '_5xhk'}))

    address = ''
    for i, pattern in enumerate(address_patterns):
        address_node = soup.find(pattern[0], pattern[1])
        if address_node is not None:
            address = address_node.text
            address_detail = address_node.find_next_sibling('div', {'class': '_5xhp fsm fwn fcg'})
            if address_detail is not None:
                address += ' ' + address_detail.text
            break

    hosts = soup.find('div', {'data-testid': 'event_permalink_feature_line'})
    if hosts is None:
        hosts = ''
    else:
        hosts = hosts['content'].replace(' & ', ', ')

    event = {'title': title,
              'description': description,
              'start_datetime': start_datetime,
              'end_datetime': end_datetime,
              'address': address,
              'hosts': hosts,
              'url': url}
    return event

def parse_facebook_events(pending_events):
    # with Display():
    # binary = FirefoxBinary('/usr/local/bin/firefox')
    # driver = webdriver.Firefox(firefox_binary=binary, executable_path='/home/flux2341/geckodriver')

    parsed_events = []

    # https://github.com/mozilla/geckodriver/releases
    binary = FirefoxBinary(config.firefox_path)
    driver = webdriver.Firefox(firefox_binary=binary, executable_path=config.geckodriver_path)
    # try:
        # driver = webdriver.Firefox()
    driver.implicitly_wait(30)
    for i in range(len(pending_events)):
        # try:
        print(str(round(i/len(pending_events)*100,2))+'%', pending_events[i])
        event = parse_facebook_event(pending_events[i], driver)
        if event is not None:
            parsed_events.append(event)
        else:
            print(f'error - maybe it is private or deleted')

        # except Exception as e:
        #     print(e)
    # except Exception as e:
    #     print(e, file=sys.stderr)
    # finally:
    if driver is not None:
        driver.quit()

    return parsed_events


names_cache = {}
def get_event_name(url):
    if url in names_cache:
        return names_cache[url]
    response = requests.get(url)
    soup = BeautifulSoup(response.text, 'html.parser')
    name = soup.html.head.title.string
    names_cache[url] = name
    return name

def main():
    print('Welcome to fb_event_scraper')
    commands = '''
Available Commands
------------------
add                   add the copied event url to the queue
add <event url>       add the given event url to queue
load                  load event urls out of copied html text
show queue            view all queued events
show names            list the names of the events
show parsed           show json of parsed events
remove id1 id2 ...    remove an event url from the queue
clear                 remove all event urls from the queue
parse                 parse all queued fb events
copy                  copy parsed events as json
help                  some hints
commands              show this list of commands
quit/exit             quit
'''
    print(commands)



    queued_events = []
    parsed_events = []


    while True:
        input_text = input('> ').split(' ', 1)
        command = input_text[0]
        if command == 'add':
            if len(input_text) == 1:
                fb_event_url = pyperclip.paste()
            else:
                fb_event_url = input_text[1].strip()
            if fb_event_url in queued_events:
                print('that event url has already been added')
            else:
                queued_events.append(fb_event_url)
        elif command == 'load':
            event_text = pyperclip.paste() # get html from clipboard
            events = re.findall(r'events\/\d+\/', event_text) # find event urls
            events = list(set(events)) # remove duplicates
            for event in events: # add to queued events
                queued_events.append('https://www.facebook.com/' + event)
            print(f'loaded {len(events)} events')
        elif command == 'show':
            which = input_text[1]
            if which == 'queue':
                print(f'queued events - {len(queued_events)}')
                for i, event in enumerate(queued_events):
                    print(f'{i}\t{event}')
            elif which == 'names':
                for i, event in enumerate(queued_events):
                    name = get_event_name(event)
                    print(f'{i}\t{name}\t{event}')
            elif which == 'parsed':
                print(f'parsed events - {len(parsed_events)}')
                for i, event in enumerate(parsed_events):
                    print('title:   ' + event['title'])
                    print('desc:    ' + event['description'])
                    print('start:   ' + event['start_datetime'])
                    print('end:     ' + event['end_datetime'])
                    print('address: ' + event['address'])
                    print('hosts:   ' + event['hosts'])
                    print('url:     ' + event['url'])
                    print('-----------------------------------------')
            else:
                print('enter \'show queue/names/parsed\'')
        elif command == 'remove':
            try:
                ids = input_text[1:]
                ids = [int(id) for id in ids]
                ids.sort(reverse=True)
                for id in ids:
                    queued_events.pop(id)
            except Exception as e:
                print(e)
        elif command == 'clear':
            queued_events = []
        elif command == 'parse':
            events = parse_facebook_events(queued_events)
            for event in events:
                parsed_events.append(event)
        elif command == 'copy':
            pyperclip.copy(json.dumps(parsed_events, indent=4))
            print('event data copied')
        elif command == 'help':
            print("""
1 Queueing event links from your personal event calendar
1.1 open https://www.facebook.com/events/calendar/ in your browser
1.2 right click on page-> inspect element
1.3 right click <html> tag -> copy -> inner html
1.4 run 'load'

2 Queueing events individually
2.1 open the facebook event page in your browser
2.2 copy the url in the address bar
2.3 run 'add'

3 Edit queued events
3.1 run 'show_names' to get a list of all the queued event ids, urls, and names
3.2 run 'remove id1 id2 ...' to remove events you don't want

4 Get JSON for loaded events
4.1 run 'parse' this will load event data from the queued event urls
4.2 run 'copy'

""")

        elif command == 'commands':
            print(commands)
        elif command == 'quit' or command == 'exit':
            quit()


if __name__ == '__main__':
    main()
