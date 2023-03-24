import React, {useEffect, useState} from 'react';
import MaterialTable from 'material-table';

import { Link } from 'react-router-dom'
import VisibilityIcon from '@material-ui/icons/Visibility';
import Tooltip from '@material-ui/core/Tooltip';
import IconButton from '@material-ui/core/IconButton';
 import axios from "axios";
import { url as baseUrl, token as token  } from "../../../api";
import {PrescriptionObj} from './PrescriptionObj';
import SplitActionButton from '../../layouts/SplitActionButton';
import {FaEye, FaUserPlus} from "react-icons/fa";

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

const DispenseList = (props) => {
  const [currentPage,setCurrentPage] = useState(1);

  function actionItems(prescription){
        console.log(prescription);
        return  [
            {
                type:'single',
                actions:[
                    {
                        name:'Dashboard',
                        type:'link',
                        icon:<FaEye  size="22"/>,
                        to:{
                            pathname: "/prescriptions",
                            state: prescription
                        }
                    }
                ]
            }

        ]
    }

    const getData = query =>
      new Promise((resolve, reject) => {
           axios.get(`${baseUrl}drug-dispenses?searchParam=${query.search}&pageNo=${query.page}&pageSize=${query.pageSize}`, { headers: {"Authorization" : `Bearer ${token}`} })
                  .then(resp => resp)
                  .then(result => {
                  if (result.data.records === null) {
                     resolve({
                        data: [],
                        page: 0,
                        totalCount: 0
                    })
                  }else{
                       resolve({
                          data: result.data.records.map((row) => ({
                            name: row.drugName,
                            dosageStrength: row.dosageStrength + " " + row.dosageStrengthUnit,
                            quantity: row.quantity,
                            unit: row.unit,
                            dateTimeDispensed: row.dateTimeDispensed.replace("@", " "),
                          })),
                          page: query.page,
                          totalCount: result.data.totalRecords
                      });
                  }
               })
      })

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
        title="Patient's Dispense History"
        columns={[
//          { title: "Patient ID", field: "Id" },
          {
            title: "Drug Name",
            field: "name",
          },
          { title: "Dosage Strength", field: "dosageStrength"},
          {
            title: "Total Quanity",
            field: "quantity",
            filtering: false,
          },
          {
              title: "Unit",
              field: "unit",
            },
          {
            title: "Date Time Dispensed",
            field: "dateTimeDispensed",
            filtering: false,
          },
        ]}
        data={ getData }
          options={{
            headerStyle: {
                backgroundColor: "#014d88",
                color: "#fff"
            },
            searchFieldStyle: {
                width : '300%',
                margingLeft: '250px',
            },
            filtering: false,
            exportButton: false,
            searchFieldAlignment: 'left',
            pageSizeOptions:[10,20,100],
            pageSize:10,
            debounceInterval: 400
        }}
        onChangePage={handleChangePage}
        localization={localization}
      />
    </div>
  );
}

export default DispenseList;


