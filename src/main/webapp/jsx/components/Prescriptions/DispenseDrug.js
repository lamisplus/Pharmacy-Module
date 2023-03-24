import React, {useState, useEffect} from 'react';
import {  Modal, ModalHeader, ModalBody,
    Col,Input,
    FormGroup,
    Label,Card, CardBody
} from 'reactstrap';

import MatButton from '@material-ui/core/Button'
import { makeStyles } from '@material-ui/core/styles'
import SaveIcon from '@material-ui/icons/Save'
import CancelIcon from '@material-ui/icons/Cancel'
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "react-widgets/styles.css";
import axios from "axios";
import { Row } from "react-bootstrap";
import { Segment,  } from 'semantic-ui-react'
import { url  as baseUrl, token} from "../../../api";
import Moment from 'moment';
import momentLocalizer from 'react-widgets-moment';
import { useHistory } from 'react-router-dom';

Moment.locale('en');
//momentLocalizer();

const useStyles = makeStyles(theme => ({
    card: {
        margin: theme.spacing(20),
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center'
    },
    form: {
        width: '100%', // Fix IE 11 issue.
        marginTop: theme.spacing(3)
    },
    submit: {
        margin: theme.spacing(3, 0, 2)
    },
    cardBottom: {
        marginBottom: 20
    },
    Select: {
        height: 45,
        width: 350
    },
    button: {
        margin: theme.spacing(1)
    },

    root: {
        '& > *': {
            margin: theme.spacing(1)
        }
    },
    input: {
        border:'1px solid #014d88',
        borderRadius:'0px',
        fontSize:'16px',
        color:'#000'
    },
    error: {
        color: "#f85032",
        fontSize: "11px",
    },
    success: {
        color: "#4BB543 ",
        fontSize: "11px",
    },
    inputGroupText:{
        backgroundColor:'#014d88',
        fontWeight:"bolder",
        color:'#fff',
        borderRadius:'0px'
    },
    label:{
        fontSize:'14px',
        color:'#014d88',
        fontWeight:'600'
    }
}))

const DispenseModal = (props) => {
    const history = useHistory();
   const drugDetails= props && props.datasample ? props.datasample : {}
   //console.log(drugDetails)
   //console.log(props)

    const { buttonLabel, className } = props;
    const toggle = props.togglestatus
    const modal = props.modalstatus
    const closeBtn = props.close
    const classes = useStyles();
    const [saving, setSaving] = useState(false);
    const [drugDispenseObj] = useState({drugDispenses:[]})
    const [formValues, setFormValues] = useState({
        brand: "",
        comment: "",
        dateTimeDispensed: "",
        dispensedBy: "",
        dosageFrequency: drugDetails.dosageFrequency,
        dosageStrength: drugDetails.dosageStrength,
        dosageStrengthUnit: drugDetails.dosageStrengthUnit,
        drugName: drugDetails.drugName,
        drugOrderId: drugDetails.id,
        duration: drugDetails.duration,
        durationUnit:drugDetails.durationUnit,
        otherDetails: {},
        patientId: drugDetails.patientId,
        quantity:"",
        startDate: drugDetails.startDate,
        type: "",
        unit: ""
    });

    const handleInputChange = (e) => {
        setFormValues ({ ...formValues, [e.target.name]: e.target.value });
    }

    const handleDispense = (e) => {
        e.preventDefault()
        drugDispenseObj.drugDispenses=[formValues]
        formValues.dateTimeDispensed=Moment(formValues.dateTimeDispensed).format("YYYY-MM-DD@HH:mm:ss")

        console.log("dispensed", drugDispenseObj)
        setSaving(true);
            axios.post(`${baseUrl}drug-dispenses`, drugDispenseObj,
            { headers: {"Authorization" : `Bearer ${token}`}},
            
            )
              .then(response => {
                  console.log(response)
                  setSaving(false);
                  const newData =  {...drugDetails, ...formValues}
                  //console.log(newData)
                  //drugDetails = newData
                  //console.log(drugDetails)
                  toast.success("Record save successful");
                  props.togglestatus()
                  history.push('/');

              })
              .catch(error => {
                console.log("error", error)
//                const newData =  {...drugDetails, ...formValues}
//                drugDetails = newData
//                console.log(drugDetails)
                  setSaving(false);
                  toast.error("Something went wrong");
                  props.togglestatus()
              });
    };

    return (
        <div>
            <Card>
                <CardBody>
                    <ToastContainer autoClose={3000} hideProgressBar />
                    <Modal
                        isOpen={modal}
                        toggle={toggle}
                        className={className}
                        size="lg"
                    >
                        <ModalHeader toggle={toggle} close={closeBtn}>
                            Dispensing
                        </ModalHeader>
                        <ModalBody>
                            <Col lg={12}>                           
                                <Row>                                    
                                    <Col xl={12} >
                                    <Segment color='teal'>
                                        <Row>
                                            <Col className="col-md-6 mb-2">
                                                <strong>Drug Name :</strong> <p>{drugDetails.drugName}</p>
                                            </Col>
                                            <Col className="col-md-6 mb-2">
                                                <strong>Date Prescribed : </strong> <p>{drugDetails.dateTimePrescribed.replace("@", " ")}</p>
                                            </Col>
                                            <Col className="col-md-6 mb-2">
                                                <strong>Dose Frequency :</strong><p>{drugDetails.dosageFrequency} daily</p>
                                            </Col>
                                            <Col className="col-md-6 mb-2">
                                                <strong>Start Date :</strong><p>{drugDetails.startDate}</p>
                                            </Col>
                                            <Col className="col-md-6 mb-1">
                                                <strong>Instruction : </strong><p>{drugDetails.comment}</p>
                                            </Col>
                                        </Row>
                                    </Segment>
                                    </Col>
                                   
                                </Row>
                                
                            </Col>
                            <br/>
                            <form>
                            <div className="row">
                                    <div className="form-group mb-3 col-md-6">
                                        <FormGroup>
                                            <Label for="maritalStatus" className={classes.label}>Date Dispensed</Label>
                                            
                                            <Input
                                                type="datetime-local"
                                                name="dateTimeDispensed"
                                                value={formValues.dateTimeDispensed}
                                                id="dateTimeDispensed"
                                                placeholder="Date Dispensed"
                                                onChange={handleInputChange}
                                                className={classes.input}
                                            />
                                        </FormGroup>
                                    </div>
                                    <div className="form-group mb-3 col-md-6">
                                        <FormGroup>
                                            <Label for="exampleNumber" className={classes.label}>Brand name</Label>
                                            <Input
                                                type="text"
                                                name="brand"
                                                value={formValues.brand}
                                                id="brand"
                                                //placeholder="brand name"
                                                onChange={handleInputChange}
                                                className={classes.input}
                                            />

                                        </FormGroup>
                                    </div>
                            </div>
                            <div className="row">

                                    <div className="form-group mb-3 col-md-6">
                                        <FormGroup>
                                            <Label className={classes.label}>Quantity</Label>
                                            <Input
                                                type="number"
                                                name="quantity"
                                                value={formValues.quantity}
                                                id="quantity"
                                                onChange={handleInputChange}
                                                className={classes.input}
                                            />
                                        </FormGroup>
                                    </div>
                                    <div className="form-group mb-3 col-md-6">
                                        <FormGroup>
                                            <Label className={classes.label}>Unit</Label>
                                            <select
                                                className="form-control"

                                                  style={{
                                                  border: "1px solid #014d88",
                                                  borderRadius:'0px',
                                                  fontSize:'14px',
                                                  color:'#000'
                                                  }}
                                                type="select"
                                                name="unit"
                                                id="unit"

                                                value={formValues.unit}
                                                onChange={handleInputChange}>
                                                <option value="Packs">Packs</option>
                                                <option value="Tablets">Tablets</option>
                                                <option value="ml">ml</option>
                                            </select>
                                        </FormGroup>
                                    </div>
                                    <div className="form-group mb-3 col-md-12">
                                        <FormGroup>
                                            <Label for="comment" className={classes.label}>Note</Label>
                                            <Input
                                                type="textarea"
                                                name="comment"
                                                id="comment"
                                                value={formValues.comment}
                                                row="40"
                                                style={{ minHeight: 100, fontSize: 14 }}
                                                onChange={handleInputChange}
                                                className={classes.input}
                                            ></Input>
                                        </FormGroup>
                                    </div>
                                </div>
                                <MatButton
                                    type="submit"
                                    variant="contained"
                                    color="primary"
                                    className={classes.button}
                                    startIcon={<SaveIcon />}
                                    onClick={handleDispense}
                                    // disabled={loading}
                                >
                                    Save
                                </MatButton>

                                <MatButton
                                    variant="contained"
                                    color="default"
                                    onClick={toggle}
                                    className={classes.button}
                                    startIcon={<CancelIcon />}>
                                    Cancel
                                </MatButton>
                            </form>
                        </ModalBody>
                    </Modal>
                </CardBody>
            </Card>
        </div>
    );
}


export default DispenseModal;
