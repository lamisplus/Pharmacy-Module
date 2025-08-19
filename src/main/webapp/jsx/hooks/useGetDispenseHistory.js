import { useCallback } from "react";
import axios from "axios";
import { url as baseUrl, token } from "../../api";


export const useGetDispenseHistory = () => {
  const fetchDispenseHistory = useCallback(async (query) => {
    try {
      const response = await axios.get(`${baseUrl}drug-dispensing/history`, {
        headers: { Authorization: `Bearer ${token}`},
      });
      return response?.data;
    } catch (error) {
      return [];
    }
  }, [baseUrl, token]);

  return { fetchDispenseHistory };
};