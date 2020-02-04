import sys
import json
import requests


api_url = 'http://sinytim.pythonanywhere.com'
url_update = api_url + '/products/update'
url_delete = api_url + '/products/delete'


if __name__ == '__main__':

    file_path = sys.argv[1]

    with open(file_path) as json_file:
        json_string = json_file.read()

    products_json = json.loads(json_string)

    url = url_update if sys.argv[2] == '-u' else url_delete if sys.argv[2] == '-d' else None

    res = requests.post(url, json=products_json)

    print('status_code: ', res.status_code)
    print('content: ', res.content)
