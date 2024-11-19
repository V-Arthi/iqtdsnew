import React, { useEffect, useState, useContext } from "react";
import * as XLSX from "xlsx";
import { useHistory } from "react-router-dom";
import { UserContext } from "../Contexts/UserContext";
import '../styles/bulkload.css';
import axios from "axios";
import { Spin, Button, message, Select } from "antd";
import { SaveOutlined } from "@ant-design/icons";
import {
  Layout, Divider, Table,
} from "antd";

const { Header, Content } = Layout;
const endpoint = process.env.REACT_APP_API_URL;

const config = require("../config.json");
//Operator list to display in select
const operatorsList = config.operators;

const constantFields = ["",
  "Claim Input Type",
  "Claim Type",
  "Tags",
  "Provider Type",
];

function BulkLoad() {

  const [currentPage, setCurrentPage] = useState(1);
  //fields list to display in select
  const [fieldsList, setFieldsList] = useState();
  //iqtds test fields coming from UI
  const [testFields, setTestFields] = useState([]);
  //operator fields coming from UI
  const [operator, setOperator] = useState([]);

  const [testCaseCol, setTestCaseCol] = useState([]);
  const [testCaseData, setTestCaseData] = useState([]);

  const [identificationCol, setIdentificationCol] = useState([]);
  const [identificationData, setIdentificationData] = useState([]);

  const [validationCol, setValidationCol] = useState([]);
  const [validationData, setValidationData] = useState([]);

  const idAndValColumns = identificationCol.concat(validationCol)

  const [loading, setLoading] = useState(false);
  const history = useHistory();
  const { authUser } = useContext(UserContext);

  useEffect(() => {
    axios
      .get(`${endpoint}/facets/all`)
      .then((res) => {
        setFieldsList([...constantFields, ...res.data])
      })
      .catch((err) => console.log(err));
  }, []);



  const handleFileUpload = (e) => {
    const file = e.target.files[0];
    const reader = new FileReader();

    reader.onload = (e) => {
      const fileData = e.target.result;
      const workbook = XLSX.read(fileData, { type: 'array' });
      const sheetName = workbook.SheetNames[0];
      const sheet = workbook.Sheets[sheetName];
      const fullData = XLSX.utils.sheet_to_json(sheet);
      console.log("Full excel Data ", fullData)

      const tcCol = []
      Object.keys(fullData.at(0)).map((key, id) => {
        if (key.includes("TestCase"))
          tcCol.push(fullData.at(0)[key]);
        return null;
      }
      )
      console.log("TestCase Columns", tcCol)
      setTestCaseCol(tcCol);

      const tcDataArray = fullData.slice(1);
      const tcDataRows = [];
      tcDataArray.map((row, index) => {
        const tcDataRow = []
        Object.keys(row).map((key, id) => {
          if (key.includes("TestCase"))
            tcDataRow.push(row[key]);
          return null;
        },
          tcDataRows.push(tcDataRow),
          console.log("TestCase Data", tcDataRow)
        )
        return null;
      })
      setTestCaseData(tcDataRows)


      const idCol = []
      Object.keys(fullData.at(0)).map((key, id) => {
        if (key.includes("Identification Condition"))
          idCol.push(fullData.at(0)[key]);
        return null;
      }
      )
      console.log("Identification Columns", idCol)
      setIdentificationCol(idCol);


      const dataArray = fullData.slice(1);
      const idDataRows = [];
      dataArray.map((row, index) => {
        const idDataRow = []
        Object.keys(row).map((key, id) => {
          if (key.includes("Identification Condition"))
            idDataRow.push(row[key]);
          return null;
        },
          idDataRows.push(idDataRow),
          console.log("Identification Data", idDataRow)
        )
        return null;
      })
      setIdentificationData(idDataRows)

      const valCol = []
      Object.keys(fullData.at(0)).map((key, id) => {
        if (key.includes("Validation Condition"))
          valCol.push(fullData.at(0)[key]);
        return null;
      }
      )
      console.log("Validation Columns", valCol)
      setValidationCol(valCol);


      const dataArray1 = fullData.slice(1);
      const valDataRows = [];
      dataArray1.map((row, index) => {
        const valDataRow = []
        Object.keys(row).map((key, id) => {
          if (key.includes("Validation Condition"))
            valDataRow.push(row[key]);
          return null;
        },
          valDataRows.push(valDataRow),
          console.log("Validation Data", valDataRow)
        )
        return null;
      })
      setValidationData(valDataRows)


    };

    reader.onerror = (error) => {
      console.log(error)
    }
    reader.readAsArrayBuffer(file);

  };

  const onChangeTestFields = (e, index) => {
    const newSelectedValues = [...testFields]
    newSelectedValues[index] = e
    setTestFields(newSelectedValues)
  }
  const onChangeOperators = (e, index) => {
    const newSelectedValues = [...operator]
    newSelectedValues[index] = e
    setOperator(newSelectedValues)
  }



  const columnsTestCase = [
    {
      title: "Excel Columns",
      dataIndex: "excelColumns",
      key: "excelColumns",
      width: "40%",
    },
    {
      title: "iQTDS Fields",
      dataIndex: "iqtdsFields",
      key: "iqtdsFields",
      width: "30%",
      render: (_, record) => (
        <Select
          showSearch
          name="testFields"
          id="testFields"
          value={testFields[record.key]}
          popupMatchSelectWidth={false}
          style={{ width: 400 }}
          onChange={(e) => onChangeTestFields(e, record.key)}
          filterOption={(input, option) => (option?.label ?? '').toLowerCase().includes(input.toLowerCase())}
        >
          {fieldsList.map((li, index) => (
            <option
              key={index}
              value={li}
              label={li}
            >{li}</option>
          ))}
        </Select>
      ),

    },
    {
      title: "Operator",
      dataIndex: "operator",
      key: "operator",
      width: "30%",
      render: (_, record) => (
        <Select
          showSearch
          name="operator"
          id="operator"
          value={operator[record.key]}
          popupMatchSelectWidth={false}
          style={{ width: 400 }}
          onChange={(e) => onChangeOperators(e, record.key)}
          filterOption={(input, option) => (option?.label ?? '').toLowerCase().includes(input.toLowerCase())}
        >
          {operatorsList.map((li, index) => (
            <option
              key={index}
              value={li}
              label={li}
            >{li}</option>
          ))}
        </Select>
      ),
    }
  ];

  const handleSave = (e) => {

    //identification data mapping
    const mapping = []
    identificationData && (
      identificationData.forEach((eachRow, id) => {

        const intermediate = []
        eachRow.forEach((cell, index) => {
          console.log()
          const obj = { "ExcelColumns": identificationCol.at(index), "iQTDSFields": testFields.at(index), "Operator": operator.at(index), "Data": cell }
          intermediate.push(obj)
        })
        //console.log(intermediate)
        mapping.push(intermediate)
      })
    );
    console.log("Identification data mapping", mapping);


    //validation data mapping
    const valMapping = []
    validationData && (
      validationData.forEach((eachRow, id) => {

        const intermediateVal = []
        eachRow.forEach((cell, index) => {
          const obj = { "ExcelColumns": validationCol.at(index), "iQTDSFields": testFields.at(identificationCol.length + index), "Operator": operator.at(identificationCol.length + index), "Data": cell }
          intermediateVal.push(obj)
        })
        //console.log(intermediate)
        valMapping.push(intermediateVal)
      })
    );
    console.log(valMapping);

    setLoading(true);
    //Identification test data condition creation
    mapping.forEach( (row, id) => {
      const test = {
        testName: "",
        claimInputType: "",
        claimType: "",
        tags: "",
        providerType: "",
        claimFilters: [],
        memberFilters: [],
        providerFilters: [],
      };
      const newClaimFilters = []
      test.testName = testCaseData[id][0] + '_Identification'
      console.log(testCaseData[id][0] + '_Identification')
      row.forEach((obj, index) => {
        //console.log(obj.iQTDSFields, obj.Operator, obj.Data)
        if (obj.iQTDSFields === "Claim Input Type")
          test.claimInputType = obj.Data
        else if (obj.iQTDSFields === "Claim Type")
          test.claimType = obj.Data
        else if (obj.iQTDSFields === "Tags")
          test.tags = obj.Data
        else if (obj.iQTDSFields === "Provider Type")
          test.providerType = obj.Data
        else if (obj.iQTDSFields != null && obj.Operator != null && obj.Data != null) {
          const newfilterItem = {
            "field": obj.iQTDSFields,
            "operator": obj.Operator,
            "value": obj.Data,
          }
          console.log(newfilterItem)
          newClaimFilters.push(newfilterItem)
          //console.log(filterItem.field,filterItem.operator,filterItem.value)
        }

      })
      test.claimFilters = newClaimFilters
      console.log(test)

      
      /*add new Identification condition */
      if (test.testName && test.claimInputType && test.claimType) {
        axios
          .post(`${endpoint}/condition/add`, test)
          .then((response) => {
            message.success(response.data);
            console.log(response.data);
          })
          .catch((error) => {
            console.log(error);
          });
      }
      else {
        console.log("Test Data Missing")
      }


    });

    // validation test data condition creation
    valMapping.forEach( (row, id) => {
      const test = {
        testName: "",
        claimInputType: "",
        claimType: "",
        tags: "",
        providerType: "",
        claimFilters: [],
        memberFilters: [],
        providerFilters: [],
      };
      const newClaimFilters = []
      test.testName = testCaseData[id][0] + '_Validation'
      console.log(testCaseData[id][0] + '_Validation')
      row.forEach((obj, index) => {
        //console.log(obj.iQTDSFields, obj.Operator, obj.Data)
        if (obj.iQTDSFields === "Claim Input Type")
          test.claimInputType = obj.Data
        else if (obj.iQTDSFields === "Claim Type")
          test.claimType = obj.Data
        else if (obj.iQTDSFields === "Tags")
          test.tags = obj.Data
        else if (obj.iQTDSFields === "Provider Type")
          test.providerType = obj.Data
        else if (obj.iQTDSFields != null && obj.Operator != null && obj.Data != null) {
          const newfilterItem = {
            "field": obj.iQTDSFields,
            "operator": obj.Operator,
            "value": obj.Data,
          }
          console.log(newfilterItem)
          newClaimFilters.push(newfilterItem)
          //console.log(filterItem.field,filterItem.operator,filterItem.value)
        }

      })
      test.claimFilters = newClaimFilters
      console.log(test)

      
      /*add new Identification condition */
      if (test.testName && test.claimInputType && test.claimType) {
        axios
          .post(`${endpoint}/condition/add`, test)
          .then((response) => {
            message.success(response.data);
            console.log(response.data);
          })
          .catch((error) => {
            console.log(error);
          });
      }
      else {
        console.log("Test Data Missing")
      }
    });

    setLoading(false);
    history.push("/design");
  };


  return (
    authUser &&
    authUser.authenticated && (
      <div className="bulk-load-div">
        {loading && (
          <Spin
            tip="please wait..."
            size="large"
            wrapperClassName="add_update"
            className="custom-spin"
          />
        )}
        <Layout className="bulk-load-layout">
          <div>
            <Header className="bulk-load-header">
              <div className="file-upload">
                <h3>Upload Excel File and Map respective columns with iQTDS Fields</h3>
                <input className="file-upload-input" type='file' accept='.xlsx , .xls' onChange={handleFileUpload} />
              </div>

              <Button
                type="primary"
                size="large"
                icon={<SaveOutlined />}
                disabled={loading}
                onClick={handleSave}
                style={{ marginRight: 10 }}
              >
                Create
              </Button>
            </Header>
            <Divider />
            <Content className="bulk-load-content">
              <Table
                className="bulk-load-table"
                bordered
                small
                columns={columnsTestCase}
                dataSource=
                {
                  Object.keys(idAndValColumns).map((row, index) => ({
                    ...row,
                    key: index,
                    excelColumns: idAndValColumns[row],
                  }))}
                pagination={{
                  pageSize: 10,
                  current: currentPage,
                  onChange: (page) => setCurrentPage(page),
                }}
              >
              </Table>
            </Content>
          </div>
        </Layout>
      </div>
    )
  );

}
export default BulkLoad;


