import { useCallback } from "react";
import axios from "axios";
import { url as baseUrl, token } from "../../api";


export const useCheckedInPatientData = () => {
  const fetchPatients = useCallback(async (query) => {
    try {
      const response = await axios.get(`${baseUrl}opd-setting`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      const data = response?.data;
      const pharmacyCode = data?.find?.(
        (item) => item.moduleServiceName.toUpperCase() === "PHARMACY"
      )?.moduleServiceCode;

      if (pharmacyCode) {
        const patientResponse = await axios.get(
          `${baseUrl}patient/checked-in-by-service/${pharmacyCode}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );

        return patientResponse?.data;
      }
    } catch (error) {
   
      return [];
    }
  }, [baseUrl, token]);

  return { fetchPatients };
};