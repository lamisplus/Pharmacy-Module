export const url =
  process.env.NODE_ENV === "development"
    ? "http://localhost:8383/api/v1/"
    : "/api/v1/";

export const token =
  process.env.NODE_ENV === "development"
    ? "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJndWVzdEBsYW1pc3BsdXMub3JnIiwiYXV0aCI6IlN1cGVyIEFkbWluIiwibmFtZSI6Ikd1ZXN0IEd1ZXN0IiwiZXhwIjoxNzUzMjM4MTM4fQ.rBqbpbk7uQgI-DkimKd7pUXB93rU2bLdG-iYytXY_KcX0Vp6tyDkwwtNCBNwm5tsX4SXVZsJdC5U51480ClL0g"
    : new URLSearchParams(window.location.search).get("jwt");