import React, { useState, Fragment, useMemo } from "react";
import { TiArrowBack } from "react-icons/ti";
import MatButton from '@material-ui/core/Button';
import { Table } from 'reactstrap'
import { makeStyles } from "@material-ui/core/styles";
import momentLocalizer from "react-widgets-moment";
import Moment from "moment";
import { Link } from "react-router-dom";
import DispenseModal from './DispenseDrug'
// import DispenseModal from './DrugDispenseFormIo';
// import DispenseModalUpdate from './DrugDispenseUpdateFormIo';
import ViewModal from './ViewModalForm';
import { Menu, MenuList, MenuButton, MenuItem } from "@reach/menu-button";
import "@reach/menu-button/styles.css";
import { Spinner } from 'reactstrap';
import {
  Card,
  CardBody,
  CardHeader,
  Col,
  Row,
} from "reactstrap";
import { forwardRef } from 'react';
import { Label } from "semantic-ui-react";
import { calculate_age } from "../../utils";
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
import CustomTable from "../Resuables/CustomTable";


Moment.locale("en");
//momentLocalizer();

const useStyles = makeStyles((theme) => ({

  card: {
    margin: theme.spacing(20),
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
  },
  form: {
    width: "100%", // Fix IE 11 issue.
    marginTop: theme.spacing(3),
  },
  submit: {
    margin: theme.spacing(3, 0, 2),
  },
  cardBottom: {
    marginBottom: 20,
  },
  Select: {
    height: 45,
    width: 350,
  },
  button: {
    margin: theme.spacing(1),
  },

  root: {
    "& > *": {
      margin: theme.spacing(1),
    },
  },

  input: {
    display: "none",
  },
}));

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

const Prescriptions = (props) => {

  // const prescriptionOrder = props && props.patientObj ? props.patientObj : {}
  const prescriptionOrder = props && props?.patientDrugOrder

  const classes = useStyles();
  const [loading, setLoading] = useState('')
  const [modal, setModal] = useState(false);
  const toggleModal = () => setModal(!modal)
  const [modal1, setModal1] = useState(false);
  const toggleModal1 = () => setModal1(!modal1)
  const [modal2, setModal2] = useState(false);
  const toggleModal2 = () => setModal2(!modal2)
  const [formData, setFormData] = useState(props && props.patientDrugOrder ? props.patientDrugOrder : []);
  const [drugDetails, setDrugDetails] = useState({})
  const [showPPI, setShowPPI] = useState(true);


  const updateFormData = (data) => {
    setLoading(true);
  }

  const toggle = (form) => {
    setDrugDetails({ ...drugDetails, ...form });
    setModal(!modal);

  }

  const toggle1 = (form) => {
    setDrugDetails({ ...drugDetails, ...form });
    setModal1(!modal1)
  }


  const toggle2 = (form) => {
    setDrugDetails({ ...drugDetails, ...form });
    setModal2(!modal2)
  }
  const closeBtn = (
    <button className="close" onClick={toggle}>
      &times;
    </button>
  );

  const closeBtn1 = (
    <button className="close" onClick={toggle1}>
      &times;
    </button>
  );


  const columns = useMemo(
    () => [
      {
        title: "Dispensing Status",
        field: "dispensingStatus",
      },
      {
        title: "Order Status",
        field: "orderStatus",
      },
      {
        title: "Dosage Amount",
        field: "dosageAmount",
      },
      // { title: "Drug brand name", field: "drugBrandName" },

      { title: "Medication Brand Name", field: "medicationName" },

      {
        title: "Duration",
        render: (data) => {
          return (
            <span>{`${data?.duration} ${data?.durationUnit}`}</span>
          )
        }
      },

      {
        title: "Quantity Prescribed",
        field: "quantityPrescribed",
        filtering: false,
      },

      {
        title: "Quantity Dispensed",
        field: "quantityDispensed",
        filtering: false,
      },

      {
        title: "Prescription Date",
        field: "prescriptionDate",
        render: (data) => {
          return (
            <span>{data?.prescriptionDate ? Moment(data.prescriptionDate).format('dddd, MMMM Do YYYY, h:mm A') : ""}</span>
          )
        }
      },
      {
        title: "Date dispensed",
        field: "dateTimeDispensed",
        render: (data) => {
          return (
            <span>{data?.dateTimeDispensed ? Moment(data.dateTimeDispensed).format('dddd, MMMM Do YYYY, h:mm A') : ""}</span>
          )
        }
      },

      {
        title: "Frequency",
        field: "frequency",
      },
      {
        title: "Medication Name",
        field: "medicationName",
      },

      {
        title: "Actions",
        field: "actions",
        render: (form) => {
          // return (
          //   <div>
          //     <ButtonGroup
          //       variant="contained"
          //       aria-label="split button"
          //       style={{
          //         backgroundColor: "rgb(153, 46, 98)",
          //         height: "30px",
          //         width: "215px",
          //       }}
          //       size="large"
          //     >
          //       <Button
          //         color="primary"
          //         size="small"
          //         aria-label="select merge strategy"
          //         aria-haspopup="menu"
          //         style={{
          //           backgroundColor: "rgb(153, 46, 98)",
          //         }}
          //       >
          //         <MdDashboard />
          //       </Button>
          //       <Button
          //         style={{
          //           backgroundColor: "rgb(153, 46, 98)",
          //         }}
          //       >
          //         <span
          //           style={{
          //             fontSize: "12px",
          //             color: "#fff",
          //             fontWeight: "bolder",
          //           }}
          //         >
          //           Action
          //         </span>
          //       </Button>
          //     </ButtonGroup>
          //   </div>
          // );
          return (
            <Menu>
              <MenuButton
                style={{
                  backgroundColor: "rgb(153, 46, 98)",
                  color: "#fff",
                  border: "2px solid #3F51B5",
                  borderRadius: "4px",
                  padding: 10
                }}
              >
                Action<span aria-hidden>▾</span>
              </MenuButton>

              <MenuList style={{ hover: "#eee" }}>
                {form && form.dispensingStatus?.toLowerCase() === "pending" || form.status === null ? (
                  <MenuItem onSelect={() =>
                    toggle(form)
                  }>
                    <i
                      className="fa fa-pencil"
                      aria-hidden="true"
                      size="15"
                      style={{ cursor: "pointer", color: "#000", fontSize: 15 }}
                    >
                      &nbsp; {""} Dispense drugs { }
                    </i>
                  </MenuItem>
                ) : (
                  <MenuItem onSelect={() => toggle(form)} >
                    <i
                      className="fa fa-pencil"
                      aria-hidden="true"
                      size="15"
                      style={{ cursor: "pointer", color: "#000" }}
                    >
                      &nbsp; {""} Update details
                    </i>
                  </MenuItem>
                )}
                {form && form.dispensingStatus?.toLowerCase() !== "pending" ? (
                  <MenuItem onSelect={() => toggle1(form)}>
                    <i
                      className="fa fa-eye"
                      aria-hidden="true"
                      size="15"
                      style={{ cursor: "pointer", color: "#000" }}
                    >
                      &nbsp; {""}View details
                    </i>
                  </MenuItem>
                )
                  :
                  ""
                }
              </MenuList>
            </Menu>
          );

        },
      },
    ],
    [showPPI]
  );


  return (
    <React.Fragment>
      <Card body>

        <div>
          <CustomTable
            key={prescriptionOrder?.length}
            title="Patient Medication Order Details"
            columns={columns}
            data={prescriptionOrder}
            icons={tableIcons}
            // showPPI={false}
            renderCheckBox={false}
          // onPPIChange={handleCheckBox}
          />
        </div>


        {modal || modal1 ?
          (
            <>
              <DispenseModal modalstatus={modal} togglestatus={toggleModal} datasample={drugDetails} updateFormData={updateFormData} />
              <ViewModal modalstatus={modal1} togglestatus={toggleModal1} datasample={drugDetails} />
            </>
          )
          : ""
        }
      </Card>
    </React.Fragment>
  );
}

export default Prescriptions;

