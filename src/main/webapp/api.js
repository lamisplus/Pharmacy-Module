export const url =
  process.env.NODE_ENV === "development"
    ? "http://localhost:8383/api/v1/"
    : "/api/v1/";

export const token =
  process.env.NODE_ENV === "development"
    ? "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJndWVzdEBsYW1pc3BsdXMub3JnIiwiYXV0aCI6IlN1cGVyIEFkbWluIiwibmFtZSI6Ikd1ZXN0IEd1ZXN0IiwiZXhwIjoxNzUzMjg3Mjk0fQ.6_sDOIxfvzXwW_0suZ_mZgUfHtpQDrz7iPtFgGwnlDswEJSbH2ryPaQRg3j_oqaU5TgUzpsSRmYcscJF-oTIOA"
    : new URLSearchParams(window.location.search).get("jwt");