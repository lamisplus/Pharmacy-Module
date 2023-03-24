

export const  token = (new URLSearchParams(window.location.search)).get("jwt")
export const url = '/api/v1/'

/*
export const url =  'http://localhost:8282/api/v1/';
export const token = 'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJndWVzdEBsYW1pc3BsdXMub3JnIiwiYXV0aCI6IlN1cGVyIEFkbWluIiwibmFtZSI6Ikd1ZXN0IEd1ZXN0IiwiZXhwIjoxNjY4NTcyMjUxfQ.Y3QT1gDV4hn6wfkL5L3mG59bJb_-Zn5doHjk2dtRFrYzzimreEIB6JIlXzy6VVLKfh7lL9Ojnh22jUYJS5x_XQ';
*/