export const url =
  process.env.NODE_ENV === "development"
    ? "http://localhost:8383/api/v1/"
    : "/api/v1/";

export const token =
  process.env.NODE_ENV === "development"
    ? "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJndWVzdEBsYW1pc3BsdXMub3JnIiwiYXV0aCI6IlN1cGVyIEFkbWluIiwibmFtZSI6Ikd1ZXN0IEd1ZXN0IiwiZXhwIjoxNzUzNjQwMDk0fQ.G_OmPpM3qlG3zK7zaVpiW2L5G_Bh9PfEaRUwPRNRlTcLoAbfdSXt_YLRnnWJfSoaN4ik8Z351kOG59j_UzhSig"
    : new URLSearchParams(window.location.search).get("jwt");