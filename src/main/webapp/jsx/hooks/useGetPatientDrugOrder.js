import { useCallback } from "react";
import axios from "axios";
import { url as baseUrl, token } from "../../api";


export const useGetPatientDrugOrder = (patientId) => {
    const fetchPatientDrugOrder = useCallback(async (query) => {
        try {
            const response = await axios.get(`${baseUrl}drug-orders/get-patient-drugOrder/${patientId}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            return response?.data;
        } catch (error) {
            return [];
        }
    }, [baseUrl, token]);

    return { fetchPatientDrugOrder };
};