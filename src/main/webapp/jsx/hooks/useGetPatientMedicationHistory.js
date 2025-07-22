import { useCallback } from "react";
import axios from "axios";
import { url as baseUrl, token } from "../../api";


export const useGetPatientMedicationHistory = (patientId) => {
  const fetchPatientMedicationHistory = useCallback(async (query) => {
    try {
      const response = await axios.get(`${baseUrl}drug-dispensing/patient/${patientId}`, {
        headers: { Authorization: `Bearer ${token}`},
      });
      return response?.data;
    } catch (error) {
      return [];
    }
  }, [baseUrl, token]);

  return { fetchPatientMedicationHistory };
};