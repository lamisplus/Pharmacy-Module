import React, { useState } from 'react';
import MaterialTable from 'material-table';
import { FaEye } from "react-icons/fa";
import { forwardRef } from 'react';
import AddBox from '@material-ui/icons/AddBox';
import ArrowUpward from '@material-ui/icons/ArrowUpward';
import Check from '@material-ui/icons/Check';
import ChevronLeft from '@material-ui/icons/ChevronLeft';
import ChevronRight from '@material-ui/icons/ChevronRight';
import Clear from '@material-ui/icons/Clear';
import DeleteOutline from '@material-ui/icons/DeleteOutline';
import Edit from '@material-ui/icons/Edit';
import FilterList from '@material-ui/icons/FilterList';
import FirstPage from '@material-ui/icons/FirstPage';
import LastPage from '@material-ui/icons/LastPage';
import Remove from '@material-ui/icons/Remove';
import SaveAlt from '@material-ui/icons/SaveAlt';
import Search from '@material-ui/icons/Search';
import ViewColumn from '@material-ui/icons/ViewColumn';
import { MTableToolbar } from "material-table";
import { useGetPatientMedicationHistory } from '../../hooks/useGetPatientMedicationHistory';


const tableIcons = {
    Add: forwardRef((props, ref) => <AddBox {...props} ref={ref} />),
    Check: forwardRef((props, ref) => <Check {...props} ref={ref} />),
    Clear: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
    Delete: forwardRef((props, ref) => <DeleteOutline {...props} ref={ref} />),
    DetailPanel: forwardRef((props, ref) => <ChevronRight {...props} ref={ref} />),
    Edit: forwardRef((props, ref) => <Edit {...props} ref={ref} />),
    Export: forwardRef((props, ref) => <SaveAlt {...props} ref={ref} />),
    Filter: forwardRef((props, ref) => <FilterList {...props} ref={ref} />),
    FirstPage: forwardRef((props, ref) => <FirstPage {...props} ref={ref} />),
    LastPage: forwardRef((props, ref) => <LastPage {...props} ref={ref} />),
    NextPage: forwardRef((props, ref) => <ChevronRight {...props} ref={ref} />),
    PreviousPage: forwardRef((props, ref) => <ChevronLeft {...props} ref={ref} />),
    ResetSearch: forwardRef((props, ref) => <Clear {...props} ref={ref} />),
    Search: forwardRef((props, ref) => <Search {...props} ref={ref} />),
    SortArrow: forwardRef((props, ref) => <ArrowUpward {...props} ref={ref} />),
    ThirdStateCheck: forwardRef((props, ref) => <Remove {...props} ref={ref} />),
    ViewColumn: forwardRef((props, ref) => <ViewColumn {...props} ref={ref} />)
};

const MedicationHistory = (props) => {
    const { patientObj } = props
    const [currentPage, setCurrentPage] = useState(1);
    const { fetchPatientMedicationHistory } = useGetPatientMedicationHistory(patientObj?.id);
    const [showPPI, setShowPPI] = useState(true);

    

    const getData = async (query) => {
        try {
            const data = await fetchPatientMedicationHistory(query);
            return {
                data: data?.content,
                page: query?.pageNumber || 0,
                totalCount: data?.content?.length || 0,
            };
        } catch (error) {
            return {
                data: [],
                page: 0,
                totalCount: 0,
            };
        }
    };



    const CustomToolbar = (props) => (
        <div>
            <div className="form-check custom-checkbox float-left mt-4 ml-3">
                <input
                    type="checkbox"
                    className="form-check-input"
                    name="showPPI"
                    id="showPPI"
                    value="showPPI"
                    checked={!showPPI}
                    onChange={() => setShowPPI(!showPPI)}
                    style={{
                        border: "1px solid #014D88",
                        borderRadius: "0.25rem",
                    }}
                />
                <label className="form-check-label" htmlFor="basic_checkbox_1">
                    <b style={{ color: "#014d88", fontWeight: "bold" }}>SHOW PII</b>
                </label>
            </div>
            <MTableToolbar {...props} />
        </div>
    );


    const handleChangePage = (page) => {
        setCurrentPage(page + 1);
    };

    const localization = {
        pagination: {
            labelDisplayedRows: `Page: ${currentPage}`
        }
    }

    return (
        <div>
            <MaterialTable
                icons={tableIcons}
                title="Dispense History"
                columns={[
                    {
                        title: "Patient Name",
                        field: "fullname",
                        hidden: showPPI,
                        render: (rowData) => (
                            <p>
                                {`${rowData?.patientFirstName} ${rowData?.patientFirstName || rowData?.lastName || ""}`}
                            </p>
                        ),
                    },
                    {
                        title: "Brand Name",
                        field: "brandName",
                    },
                    { title: "Medication Name", field: "medicationName" },
                    
                    {
                        title: "Prescription Date",
                        field: "prescriptionDate",
                    },
                    {
                        title: "Date dispensed",
                        field: "dateTimeDispensed",
                    },
                    {
                        title: "Quantity Unit",
                        field: "quantityUnit",
                        filtering: false,
                    },
                    {
                        title: "Quantity Dispensed",
                        field: "quantityDispensed",
                        filtering: false,
                    },
                    {
                        title: "Frequency",
                        field: "frequency",
                        filtering: false,
                    },
                    {
                        title: "Formulation",
                        field: "formulation",
                        filtering: false,
                    },
                    {
                        title: "Strength",
                        field: "strength",
                        filtering: false,
                    },
                    {
                        title: "Date Dispensed",
                        field: "dateTimeDispensed",
                        filtering: false,
                    },
                ]}
                data={getData || []}
                options={{
                    headerStyle: {
                        backgroundColor: "#014d88",
                        color: "#fff"
                    },
                    searchFieldStyle: {
                        width: '300%',
                        margingLeft: '250px',
                    },
                    filtering: false,
                    exportButton: false,
                    searchFieldAlignment: 'left',
                    pageSizeOptions: [10, 20, 100],
                    pageSize: 10,
                    debounceInterval: 400
                }}
                onChangePage={handleChangePage}
                localization={localization}
            />
        </div>
    );
}

export default MedicationHistory;


