import React, { useEffect, useState, useContext } from "react";
import { Link } from "react-router-dom";
import { UserContext } from "../Contexts/UserContext";
import axios from "axios";
import {
  Button,
  Alert,
  Divider,
  Popover,
  Input,
  Select,
  message,
  Table,
  Space,
} from "antd";
import { QuestionCircleFilled, CaretRightOutlined,} from "@ant-design/icons";
import { v4 as uuid } from "uuid";

const config = require("../config.json");
const endpoint = process.env.REACT_APP_API_URL;
const environments = config.environments;

const emptyAlert = { type: "info", message: "" };

const CONNECTION_ERROR =
  "Error in connecting to api server, Please contact administrator";

const columns = [
  { title: "ID #", dataIndex: "id", key: "id" },
  { title: "Testcase Name", dataIndex: "name", key: "name" },
  { title: "Identification condition", dataIndex: "identificationid", key: "identificationid" },
  { title: "Validation condition", dataIndex: "validationid", key: "validationid" },
  { title: "User", dataIndex: "user", key: "user" },
];

const executioncolumns = [
  { title: "ID #", dataIndex: "id", key: "id" },
  { title: "Testcase IDs", dataIndex: "tests", key: "tests" },
  { title: "Environment", dataIndex: "env", key: "env" },
  { title: "Status", dataIndex: "status", key: "status" },
  { title: "Job ID", dataIndex: "jobID", key: "jobID" , render: (id) => (
    <Link to={{ pathname: `/jobs/view/${id}` }} style={{ display: "block" }}>
      {id}
    </Link>
  ), },
  { title: "Patient Account Number", dataIndex: "paaccno", key: "paaccno" },
  { title: "User", dataIndex: "user", key: "user" },
];

const envOptions = environments.map((env) => (
  <option key={uuid()} value={env}>
    {env}
  </option>
));

function Testcases(props) {
  const { authUser } = useContext(UserContext);
  const [testcase,] = useState({ name: "", identificationid: 0, validationid: 0, user: authUser.name });
  const [testcases, setTestcases] = useState(null);
  const [testcaseexecution, setTestcasesExecution] = useState(null);
  const [alert, setAlert] = useState(emptyAlert);
  const [tests, setTests] = useState([]);
  const [allEnv, setAllEnv] = useState(environments[0]);
  const [hasSelected, setHasSelected] = useState(false);
  const [selectedTests, ] = useState({tests:"", env:allEnv, user:authUser.name});

  function loadTest() {
    axios
      .get(`${endpoint}/conditions-lite`)
      .then((res) => {
        setTests(res.data);
      })
      .catch((err) => {
        if (err.response) {
          setAlert({ type: "error", message: err.response.data });
        } else {
          setAlert({ type: "error", message: CONNECTION_ERROR });
        }
      });
  }

  function loadData() {
    axios
      .get(`${endpoint}/testcase/all`)
      .then((res) => {
        setTestcases(res.data)
      })
      .catch((err) => message.error("error while loading mapping data"));
  }

  function loadExecutionData() {
    axios
      .get(`${endpoint}/testcase/allexecution`)
      .then((res) => {
        setTestcasesExecution(res.data)
      })
      .catch((err) => message.error("error while loading mapping data"));
  }

  useEffect(() => {
    loadTest();
    loadData();
    loadExecutionData();
  }, []);

  const rowSelection = {
    onChange: (selectedRowKeys, selectedRows) => {
      console.log(selectedRowKeys);
      if (selectedRows.length >0){
        selectedTests.tests = selectedRowKeys;
        setHasSelected(true)
      }else{
        setHasSelected(false)
      }
    },
  };

  function execute() {
    selectedTests.env = allEnv;
    console.log(selectedTests)
    axios
      .post(`${endpoint}/testcase/execute`, selectedTests)
      .then((res) => {
        message.success(res.data);
      })
      .catch((err) => message.error(err.res.data));
  }

  const requestNew = (e) => {
    e.preventDefault();
    console.log(testcase)
    if (!testcase.name || !testcase.identificationid || !testcase.validationid || !testcase.user) {
      setAlert({ type: "error", message: "All fields are mandatory" });
      return;
    }

    axios
      .post(`${endpoint}/testcase/add`, testcase)
      .then((res) => {
        message.success(res.data);
      })
      .catch((err) => message.error(err.res.data));
  }

  return (
    authUser &&
    authUser.authenticated && (
      <div className="claim-validation-container">
        {alert.message && (
          <Alert
            type={alert.type}
            message={alert.message}
            showIcon
            closable
            onClose={() => setAlert({ type: "info", message: "" })}
            style={{ marginBottom: 30 }}
          />
        )}
        {
          <div className="claim-validation-request-form-container">
            <form
              onSubmit={(e) => requestNew(e)}
            // onReset={resetForm}
            >
              <Space>
                <Input
                  name="name"
                  placeholder="Testcase Name"
                  className="mapping_form_field"
                  style={{ width: 400, }}
                  onChange={(e) => testcase.name = e.target.value}
                />

                <Select
                  showSearch
                  name="identificationid"
                  style={{ width: 400, }}
                  placeholder="Test Data Identification Condition"
                  optionFilterProp="children"
                  filterOption={(input, option) => (option?.label ?? '').includes(input)}
                  onChange={(e) => testcase.identificationid = e}
                >
                  {tests.map((test) => (
                    <option
                      key={test.testId}
                      value={test.testId}
                      label={`${test.testId} - ${test.testName}`}
                    >{`${test.testId} - 
                    ${test.testName}`}</option>
                  ))}
                </Select>

                <Select
                  showSearch
                  id="validationid"
                  style={{ width: 400, }}
                  placeholder="Test Data Validation Condition"
                  optionFilterProp="children"
                  filterOption={(input, option) => (option?.label ?? '').includes(input)}
                  onChange={(e) => testcase.validationid = e}
                >
                  {tests.map((test) => (
                    <option
                      key={test.testId}
                      value={test.testId}
                      label={`${test.testId} - ${test.testName}`}
                    >{`${test.testId} - ${test.testName}`}</option>
                  ))}
                </Select>

                <Button type="primary" htmlType="submit" className="submit-btn">
                  Add
                </Button>
                <Button type="ghost" htmlType="reset">
                  Clear
                </Button>
              </Space>
            </form>
            <Popover
              content={
                <ul> Select test data indentification condition and
                  test data validation condition to map it to test case
                </ul>
              }
              title="Instructions"
            >
              <Button
                icon={<QuestionCircleFilled />}
                type="link"
                style={{ marginLeft: 30 }}
              />
            </Popover>
          </div>
        }
        <Divider />

        {testcases && (
          <div
            style={{ marginBottom: 16 }}
            className="execution-container__header"
          >
            <div className="execution-container__selection">
              <Button
                onClick={execute}
                disabled={!hasSelected}
                icon={<CaretRightOutlined />}
                type="primary"
              >
                Run
              </Button>
              <span style={{ marginLeft: 8 }}>
              </span>
            </div>
            <div className="execution-container__enviroment">
              <label htmlFor="environment__select" style={{ marginRight: 10 }}>
                Environment
              </label>
              <select
                name="environment__select"
                value={allEnv}
                onChange={(e) => setAllEnv(e.target.value)}
              >
                {envOptions}
              </select>
            </div>
            </div>
            )}
            {testcases && (
          <Table
          title={() => "Testcases"}
          rowSelection={
           {
              type: "checkbox",
              ...rowSelection,
            }
          }
            columns={columns}
            dataSource={testcases.map((d) => ({ ...d, 
              key: d.id, 
              identificationid: 
              tests.filter((test)=> test.testId === d.identificationid).map((test)=>{
                    return (`${test.testId} - ${test.testName}`)
                }),  
              validationid: 
              tests.filter((test)=> test.testId === d.validationid).map((test)=>{
                return (`${test.testId} - ${test.testName}`)
            }),
            
            }))}
            size="small"
          />
        )}
        <Divider />
        {testcaseexecution && (
          <Table
          title={() => "Testcases Execution"}
            columns={executioncolumns}
            dataSource={testcaseexecution.map((d) => ({ ...d, key: d.id, }))}
            size="small"
          />
        )}
      </div>
    ))
}

export default Testcases;
