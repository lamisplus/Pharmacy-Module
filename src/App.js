import React from "react";
import {
  MemoryRouter as Router,
  Switch,
  Route,
} from "react-router-dom";
import { ToastContainer} from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import "./main/webapp/vendor/bootstrap-select/dist/css/bootstrap-select.min.css";
import "./../src/main/webapp/css/style.css";
import Home from './main/webapp/jsx/components/Home'
import Prescription from './main/webapp/jsx/components/Prescriptions/Index'

export default function App() {
  return (

      <div>
      <ToastContainer />
        <Switch>
          <Route path="/prescriptions">
            <Prescription />
          </Route>
          <Route path="/">
            <Home />
          </Route>
        </Switch>
      </div>
 
  );
}




