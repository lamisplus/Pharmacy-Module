import React, { useEffect, useState, useMemo } from 'react';
import MaterialTable from 'material-table';

import { Link } from 'react-router-dom'
import VisibilityIcon from '@material-ui/icons/Visibility';
import Tooltip from '@material-ui/core/Tooltip';
import IconButton from '@material-ui/core/IconButton';
import axios from "axios";
import { url as baseUrl, token as token } from "../../../api";
import { PrescriptionObj } from './PrescriptionObj';
import SplitActionButton from '../../layouts/SplitActionButton';
import { FaEye, FaUserPlus } from "react-icons/fa";
import { Label } from "semantic-ui-react";
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
import CustomTable from '../Resuables/CustomTable';
import { useCheckedInPatientData } from '../../hooks/useGetCheckedInPatients';
import { Card, CardBody } from "reactstrap";
import Button from "@material-ui/core/Button";
import ButtonGroup from "@material-ui/core/ButtonGroup";
import { calculate_age } from '../../utils';
import { TiArrowForward } from "react-icons/ti";
import { MdDashboard } from "react-icons/md";


const PatientSearch = (props) => {
  const tableRef = React.createRef();
  const [currentPage, setCurrentPage] = useState(1);
  const prescriptions = PrescriptionObj;
  const [loading, setLoading] = useState("")
  const [showPPI, setShowPPI] = useState(true);
  const { fetchPatients } = useCheckedInPatientData();
  const [tableRefreshTrigger, setTableRefreshTrigger] = useState(0);

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
  //Get total drug dispensed 
  const totalDrugsPrescribed = (drugsArray) => {
    const dispensed = []
    drugsArray.map(drugs => {
      if (drugs.status === 1)
        dispensed.push(drugs)
    })
    return dispensed.length
  }
  const drugType = (drugsArray) => {
    //console.log(prescriptions)
    const type = []
    drugsArray.map(drugs => {
      if (drugs.data.type === 1) {
        type.push('Drug')
      } else if (drugs.data.type === 0) {
        type.push('Regimen')
      } else {
        type.push('')
      }

    })

    return type
  }
  function actionItems(prescription) {
    //console.log(prescription);
    return [
      {
        type: 'single',
        actions: [
          {
            name: 'Dashboard',
            type: 'link',
            icon: <FaEye size="22" />,
            to: {
              pathname: "/prescriptions",
              state: prescription
            }
          }
        ]
      }

    ]
  }

  // const getData = query =>
  //   new Promise((resolve, reject) => {
  //     axios.get(`${baseUrl}drug-orders/patients?searchParam=${query.search}&pageNo=${query.page}&pageSize=${query.pageSize}`, { headers: { "Authorization": `Bearer ${token}` } })
  //       .then(resp => resp)
  //       .then(result => {
  //         if (result?.data?.records === null || !result?.data) {
  //           resolve({
  //             data: [],
  //             page: 0,
  //             totalCount: 0
  //           })
  //         } else {
  //           resolve({
  //             data: result?.data?.records?.map?.((row) => ({
  //               Id: row?.patientHospitalNumber,
  //               name: row.patientFirstName + " " + row.patientLastName,
  //               date: row.drugOrders[0].dateTimePrescribed.replace("@", " "),
  //               prescribedCount: row.drugOrders.length,
  //               dispensedCount: totalDrugsPrescribed(row?.drugOrders),
  //               // type:   drugType(prescription.formDataObj),
  //               actions: (
  //                 <div>
  //                   <SplitActionButton actions={actionItems(row)} />
  //                 </div>
  //               ),
  //             })),
  //             page: query.page,
  //             totalCount: result.data.totalRecords
  //           });
  //         }
  //       })
  //   })

  const handleChangePage = (page) => {
    setCurrentPage(page + 1);
  };

  const handleCheckBox = (e) => {
    setShowPPI(!e.target.checked);
  };

  const getData = async (query) => {
    try {
      const data = await fetchPatients(query);
      const reversedData = [...(data || [])].reverse();

      // Sync localStorage with API response
      const now = new Date();
      const storedPatients = JSON.parse(localStorage.getItem("patientsQueue")) || {};
      const updatedPatients = {};

      reversedData.forEach((patient) => {
        if (!storedPatients[patient.id]) {
          // Add new patients with a timestamp
          updatedPatients[patient.id] = { ...patient, timestamp: now };
        } else {
          updatedPatients[patient.id] = storedPatients[patient.id];
        }
      });

      // Remove patients no longer in the queue
      Object.keys(storedPatients).forEach((id) => {
        if (!updatedPatients[id]) {
          delete storedPatients[id];
        }
      });

      localStorage.setItem("patientsQueue", JSON.stringify(updatedPatients));
      return {
        data: reversedData,
        page: query?.page || 0,
        totalCount: reversedData.length || 0,
      };
    } catch (error) {
      return {
        data: [],
        page: 0,
        totalCount: 0,
      };
    }
  };

  const getHospitalNumber = (identifier) => {
    const identifiers = identifier;
    const hospitalNumber = identifiers.identifier.find(
      (obj) => obj?.type == "HospitalNumber"
    );
    return hospitalNumber ? hospitalNumber.value : "";
  };


  const columns = useMemo(
    () => [
      {
        title: "Patient Name",
        field: "fullname",
        hidden: showPPI,
        render: (rowData) => (
          <p>
            {`${rowData?.firstName} ${rowData?.surname || rowData?.lastName}`}
          </p>
        ),
      },
      {
        title: "Hospital Number",
        field: "hospitalNumber",
        render: (rowData) => (
          <p>
            {rowData?.hospitalNumber || getHospitalNumber?.(rowData?.identifier) || ""}
          </p>
        ),
      },
      { title: "Sex", field: "sex" },
      {
        title: "Age", field: "age",
        render: (rowData) => (
          <p>
            {
              rowData?.dateOfBirth === 0 ||
                rowData?.dateOfBirth === undefined ||
                rowData?.dateOfBirth === null ||
                rowData?.dateOfBirth === ""
                ? 0
                : calculate_age(rowData?.dateOfBirth)
            }
          </p>
        )
      },

      {
        title: "Biometrics",
        field: "biometricStatus",
        render: (rowData) =>
          rowData.biometricStatus === true ? (
            <Label color="green" size="mini">
              Biometric Captured
            </Label>
          ) : (
            <Label color="red" size="mini">
              No Biometric
            </Label>
          ),
      },
      {
        title: "ART Status",
        field: "currentStatus",
        render: (rowData) => (
          <Label color="blue" size="mini">
            {rowData?.currentStatus || "Not Enrolled"}
          </Label>
        ),
      },
      {
        title: "Actions",
        field: "actions",
        render: (rowData) => {
          const isOnHts = rowData?.isOnHts;

          return (
            <div>
              <Link
                to={{
                  pathname: "/prescriptions",
                  state: {
                    patientObject: rowData,
                    patientObj: rowData,
                    clientCode: rowData?.clientCode,
                    // activepage: isOnHts ? "home" : "NEW HTS",
                    checkedInPatient: true
                  },

                }}
              >
                <ButtonGroup
                  variant="contained"
                  aria-label="split button"
                  style={{
                    backgroundColor: "rgb(153, 46, 98)",
                    height: "30px",
                    width: "215px",
                  }}
                  size="large"
                >
                  <Button
                    color="primary"
                    size="small"
                    aria-label="select merge strategy"
                    aria-haspopup="menu"
                    style={{
                      backgroundColor: "rgb(153, 46, 98)",
                    }}
                  >
                    {isOnHts ? <MdDashboard /> : <TiArrowForward />}
                  </Button>
                  <Button
                    style={{
                      backgroundColor: "rgb(153, 46, 98)",
                    }}
                  >
                    <span
                      style={{
                        fontSize: "12px",
                        color: "#fff",
                        fontWeight: "bolder",
                      }}
                    >
                      {isOnHts ? "Patient Dashboard" : "Enroll Patient"}
                    </span>
                  </Button>
                </ButtonGroup>
              </Link>
            </div>
          );
        },
      },
    ],
    [showPPI]
  );

  const localization = {
    pagination: {
      labelDisplayedRows: `Page: ${currentPage}`
    }
  }

  return (
    <div>
      <Card>
        <CardBody>
          <CustomTable
            key={tableRefreshTrigger}
            title="Prescriptions"
            columns={columns}
            data={getData}
            icons={tableIcons}
            showPPI={showPPI}
            renderCheckBox
            onPPIChange={handleCheckBox}
          />
        </CardBody>
      </Card>
    </div>
  );
}

export default PatientSearch;


