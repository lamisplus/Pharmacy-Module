import React, { useState, useEffect } from 'react';
import {
    Modal, ModalHeader, ModalBody,
    Col, Input,
    FormGroup,
    Label, Card, CardBody
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
import { Segment, } from 'semantic-ui-react'
import { url as baseUrl, token } from "../../../api";
import Moment from 'moment';
import momentLocalizer from 'react-widgets-moment';
import { useHistory } from 'react-router-dom';
import { useGetPatientDrugOrder } from '../../hooks/useGetPatientDrugOrder';

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
        border: '1px solid #014d88',
        borderRadius: '0px',
        fontSize: '16px',
        color: '#000'
    },
    error: {
        color: "#f85032",
        fontSize: "11px",
    },
    success: {
        color: "#4BB543 ",
        fontSize: "11px",
    },
    inputGroupText: {
        backgroundColor: '#014d88',
        fontWeight: "bolder",
        color: '#fff',
        borderRadius: '0px'
    },
    label: {
        fontSize: '14px',
        color: '#014d88',
        fontWeight: '600'
    }
}))

const DispenseModal = (props) => {
    const history = useHistory();
    const drugDetails = props && props.datasample ? props.datasample : {}

    console.log("drug detail", drugDetails)


    const { buttonLabel, className } = props;
    const toggle = props.togglestatus
    const modal = props.modalstatus
    const closeBtn = props.close
    const classes = useStyles();
    const [saving, setSaving] = useState(false);
    const [drugDispenseObj] = useState({ drugDispenses: [] })
    const [formValues, setFormValues] = useState({
        drugOrderId: drugDetails?.drugOrderId ||drugDetails?.id,
        patientId: drugDetails?.patientId,
        medicationName: drugDetails.medicationName,
        drugBrandName: drugDetails?.drugBrandName,
        manufacturer: drugDetails?.manufacturer,
        batchNumber: drugDetails?.batchNumber,
        expiryDate: drugDetails?.expiryDate,
        quantityDispensed: drugDetails?.quantityDispensed,
        quantityUnit: drugDetails?.quantityUnit,
        formulation: drugDetails?.formulation,
        strength: drugDetails?.strength,
        dispensingNotes: drugDetails?.dispensingNotes,
        isRefill: drugDetails?.isRefill,
        notes: drugDetails?.notes,
        refillNumber: drugDetails?.refillNumber,
        substitutionMade: drugDetails?.substitutionMade,
        substitutionReason: drugDetails?.substitutionReason
    });

    const handleInputChange = (e) => {
        setFormValues({ ...formValues, [e.target.name]: e.target.value });
    }

    const handleDispense = (e) => {
        e.preventDefault()
        // drugDispenseObj.drugDispenses = [formValues]
        // formValues.dateTimeDispensed = Moment(formValues.dateTimeDispensed).format("YYYY-MM-DD@HH:mm:ss")
        setSaving(true);
        axios.post(`${baseUrl}drug-dispensing/dispense`, formValues,
            { headers: { "Authorization": `Bearer ${token}` } },
        )
            .then(response => {
                setSaving(false);
                const newData = { ...drugDetails, ...formValues }
                toast.success("Record save successful");
                props.togglestatus()
                history.push('/');

            })
            .catch(error => {
                console.log("error", error)
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
                                                    <strong>Medication Name :</strong> <p>{drugDetails.medicationName}</p>
                                                </Col>
                                                <Col className="col-md-6 mb-2">
                                                    <strong>Date Prescribed : </strong> <p>{drugDetails?.prescriptionDate ? Moment(drugDetails.prescriptionDate).format('dddd, MMMM Do YYYY, h:mm A') : ""}</p>
                                                </Col>
                                                <Col className="col-md-6 mb-2">
                                                    <strong>Dose Frequency :</strong><p>{drugDetails.frequency} daily</p>
                                                </Col>
                                                <Col className="col-md-6 mb-2">
                                                    <strong>Start Date :</strong><p>{drugDetails?.startDate ? Moment(drugDetails.startDate).format('dddd, MMMM Do YYYY, h:mm A') : ""}</p>
                                                </Col>
                                                <Col className="col-md-6 mb-1">
                                                    <strong>Instruction : </strong><p>{drugDetails.notes}</p>
                                                </Col>
                                            </Row>
                                        </Segment>
                                    </Col>

                                </Row>

                            </Col>
                            <br />
                            <form>
                                <div className="row">
                                    <div className="form-group mb-3 col-md-6">
                                        <FormGroup>
                                            <Label for="dateTimeDispensed" className={classes.label}>Date Dispensed</Label>

                                            <Input
                                                type="datetime-local"
                                                name="dateTimeDispensed"
                                                value={formValues.startDate}
                                                id="dateTimeDispensed"
                                                placeholder="Date Dispensed"
                                                onChange={handleInputChange}
                                                className={classes.input}
                                            />
                                        </FormGroup>
                                    </div> 
                                    {/* <div className="form-group mb-3 col-md-6">
                                        <FormGroup>
                                            <Label for="medicationName" className={classes.label}>Medication name</Label>
                                            <Input
                                                type="text"
                                                name="medicationName"
                                                value={formValues?.medicationName}
                                                id="medicationName"
                                                //placeholder="brand name"
                                                onChange={handleInputChange}
                                                className={classes.input}
                                            />

                                        </FormGroup>
                                    </div> */}

                                    <div className="form-group mb-3 col-md-6">
                                        <FormGroup>
                                            <Label for="drugBrandName" className={classes.label}>Medication brand name</Label>
                                            <Input
                                                type="text"
                                                name="drugBrandName"
                                                value={formValues?.drugBrandName}
                                                id="drugBrandName"
                                                //placeholder="brand name"
                                                onChange={handleInputChange}
                                                className={classes.input}
                                            />

                                        </FormGroup>
                                    </div>

                                    {/* <div className="form-group mb-3 col-md-6">
                                        <FormGroup>
                                            <Label for="manufacturer" className={classes.label}>Manufacturer</Label>
                                            <Input
                                                type="text"
                                                name="manufacturer"
                                                value={formValues?.manufacturer}
                                                id="manufacturer"
                                                //placeholder="brand name"
                                                onChange={handleInputChange}
                                                className={classes.input}
                                            />

                                        </FormGroup>
                                    </div> */}

                                    {/* <div className="form-group mb-3 col-md-6">
                                        <FormGroup>
                                            <Label for="batchNumber" className={classes.label}>Batch number</Label>
                                            <Input
                                                type="text"
                                                name="batchNumber"
                                                value={formValues?.batchNumber}
                                                id="batchNumber"
                                                //placeholder="brand name"
                                                onChange={handleInputChange}
                                                className={classes.input}
                                            />

                                        </FormGroup>
                                    </div> */}

                                    {/* <div className="form-group mb-3 col-md-6">
                                        <FormGroup>
                                            <Label for="expiryDate" className={classes.label}>Expiry date</Label>
                                            <Input
                                                type="date"
                                                name="expiryDate"
                                                value={formValues?.expiryDate}
                                                id="expiryDate"
                                                //placeholder="brand name"
                                                onChange={handleInputChange}
                                                className={classes.input}
                                            />

                                        </FormGroup>
                                    </div> */}

                                    <div className="form-group mb-3 col-md-6">
                                        <FormGroup>
                                            <Label for="quantityDispensed" className={classes.label}>Quantity dispensed</Label>
                                            <Input
                                                type="number"
                                                name="quantityDispensed"
                                                value={formValues?.quantityDispensed}
                                                id="quantityDispensed"
                                                //placeholder="brand name"
                                                onChange={handleInputChange}
                                                className={classes.input}
                                            />

                                        </FormGroup>
                                    </div>

                                    <div className="form-group mb-3 col-md-6">
                                        <FormGroup>
                                            <Label for="quantityUnit" className={classes.label}>Quantity unit</Label>
                                            <Input
                                                type="number"
                                                name="quantityUnit"
                                                value={formValues?.quantityUnit}
                                                id="quantityUnit"
                                                //placeholder="brand name"
                                                onChange={handleInputChange}
                                                className={classes.input}
                                            />

                                        </FormGroup>
                                    </div>

                                    {/* <div className="form-group mb-3 col-md-6">
                                        <FormGroup>
                                            <Label for="formulation" className={classes.label}>Formulation</Label>
                                            <Input
                                                type="text"
                                                name="formulation"
                                                value={formValues?.formulation}
                                                id="formulation"
                                                //placeholder="brand name"
                                                onChange={handleInputChange}
                                                className={classes.input}
                                            />

                                        </FormGroup>
                                    </div> */}

                                    {/* <div className="form-group mb-3 col-md-6">
                                        <FormGroup>
                                            <Label for="strength" className={classes.label}>Strength</Label>
                                            <Input
                                                type="text"
                                                name="strength"
                                                value={formValues?.strength}
                                                id="strength"
                                                //placeholder="brand name"
                                                onChange={handleInputChange}
                                                className={classes.input}
                                            />

                                        </FormGroup>
                                    </div> */}

                                </div>
                                <div className="row">
                                    {/* <div className="form-group mb-3 col-md-6">
                                        <FormGroup>
                                            <Label className={classes.label}>Quantity</Label>
                                            <Input
                                                type="number"
                                                name="quantity"
                                                value={formValues.quantityUnit}
                                                id="quantity"
                                                onChange={handleInputChange}
                                                className={classes.input}
                                            />
                                        </FormGroup>
                                    </div> */}

                                    {/* <div className="form-group mb-3 col-md-6">
                                        <FormGroup>
                                            <Label className={classes.label}>Unit</Label>
                                            <select
                                                className="form-control"

                                                style={{
                                                    border: "1px solid #014d88",
                                                    borderRadius: '0px',
                                                    fontSize: '14px',
                                                    color: '#000'
                                                }}
                                                type="select"
                                                name="unit"
                                                id="unit"

                                                value={formValues.quantityUnit}
                                                onChange={handleInputChange}>
                                                <option value="Packs">Packs</option>
                                                <option value="Tablets">Tablets</option>
                                                <option value="ml">ml</option>
                                            </select>
                                        </FormGroup>
                                    </div> */}

                                    {/* <div className="form-group mb-3 col-md-12">
                                        <FormGroup>
                                            <Label className={classes.label}>Substitution Made</Label>
                                            <Input
                                                type="text"
                                                name="substitutionMade"
                                                value={formValues.substitutionMade}
                                                id="substitutionMade"
                                                onChange={handleInputChange}
                                                className={classes.input}
                                            />
                                        </FormGroup>
                                    </div> */}

                                    <div className="form-group mb-3 col-md-12">
                                        <FormGroup>
                                            <Label for="substitutionReason" className={classes.label}>Reason for substitution</Label>
                                            <Input
                                                type="textarea"
                                                name="substitutionReason"
                                                id="substitutionReason"
                                                value={formValues.substitutionReason}
                                                row="40"
                                                style={{ minHeight: 100, fontSize: 14 }}
                                                onChange={handleInputChange}
                                                className={classes.input}
                                            ></Input>
                                        </FormGroup>
                                    </div>

                                    <div className="form-group mb-3 col-md-12">
                                        <FormGroup>
                                            <Label for="notes" className={classes.label}>Note</Label>
                                            <Input
                                                type="textarea"
                                                name="notes"
                                                id="notes"
                                                value={formValues.notes}
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
