import React, { useState, useEffect } from 'react';
import { withStyles } from '@material-ui/core/styles';
import 'semantic-ui-css/semantic.min.css';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import PatientCardDetail from './../Patient/PatientCard';
import PatientPrescriptions from './PatientPrescriptions';
import { useHistory } from "react-router-dom";
import axios from "axios"
import { url, token } from '../../../api';
import {  Tab, Tabs, } from "react-bootstrap";
import MedicationHistory from '../Medication/MedicationHistory';


const styles = theme => ({
  root: {
    width: '100%',
  },
  heading: {
    fontSize: theme.typography.pxToRem(15),
  },
  secondaryHeading: {
    fontSize: theme.typography.pxToRem(15),
    color: theme.palette.text.secondary,
  },
  icon: {
    verticalAlign: 'bottom',
    height: 20,
    width: 20,
  },
  details: {
    alignItems: 'center',
  },
  column: {
    flexBasis: '20.33%',
  },
  helper: {
    borderLeft: `2px solid ${theme.palette.divider}`,
    padding: `${theme.spacing.unit}px ${theme.spacing.unit * 2}px`,
  },
  link: {
    color: theme.palette.primary.main,
    textDecoration: 'none',
    '&:hover': {
      textDecoration: 'underline',
    },
  },
});

function PatientCard(props) {
  let history = useHistory();
  const [key, setKey] = useState('drug-order');
  const { classes } = props;
  const [patientDrugOrder, setPatientDrugOrder] = useState([])


  const patientObj = history.location && history.location.state ? history.location.state?.patientObj : {}

  const fetchPatientDrugOrder = (patientId) => {
    axios.get(`${url}drug-orders/get-patient-drugOrder/${patientId}`, {
      headers: { Authorization: `Bearer ${token}` },
    }).then((res) => {
      setPatientDrugOrder(res?.data)
    }).catch((err) => {
      console.log(err)
    })
  }

  useEffect(() => {
    fetchPatientDrugOrder(patientObj?.id)
  }, [])

  return (
    <div className={classes.root}>

      <Card >
        <CardContent>
          <PatientCardDetail patientObj={patientObj} />
          <Tabs
            id="controlled-tab-example"
            activeKey={key}
            onSelect={(k) => setKey(k)}
            className="mb-3"
            style={{marginTop: 30}}
          >
            <Tab eventKey="drug-order" title="Drug Order">
              <div >
                <br />
                <PatientPrescriptions patientObj={patientObj} patientDrugOrder={patientDrugOrder} />
              </div>
            </Tab>

            <Tab eventKey="medical-history" title="Medication History">
              <MedicationHistory patientObj={patientObj}/>
            </Tab>
          </Tabs>

        </CardContent>
      </Card>
    </div>
  );
}

export default withStyles(styles)(PatientCard);
