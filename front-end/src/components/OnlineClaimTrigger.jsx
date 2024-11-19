import React, { useState, useEffect } from "react";
import { useHistory } from "react-router-dom";
import {
  Button,
  Steps,
  Alert,
  Layout,
  Collapse,
  Divider,
  Table,
  message,
} from "antd";
import { PageHeader } from '@ant-design/pro-layout';
import { SyncOutlined } from "@ant-design/icons";
import axios from "axios";
import { Space } from "antd";
import "../styles/online-claim.css";
import { v4 as uuid } from "uuid";
const { Step } = Steps;
const { Content } = Layout;
const { Panel } = Collapse;

const endpoint = process.env.REACT_APP_API_URL;


function ComboBox({ label, list, value, onChange, disabled }) {
  return (
    <div className="custom_field custom_combo">
      <label htmlFor={label}>{label}</label>
      <select
        value={value}
        name={label}
        onChange={onChange}
        disabled={disabled}
      >
        {list.map((li, index) => (
          <option key={index} value={li}>
            {li}
          </option>
        ))}
      </select>
    </div>
  );
}

const columns = [
  { title: "Build ID", dataIndex: "buildID" },
  { title: "Input Claim", dataIndex: "inputDataID" },
  { title: "Env", dataIndex: "env" },
  { title: "Created Claim ID", dataIndex: "createdDataID" },
  { title: "Created On", dataIndex: "createdOn" },
  { title: "", dataIndex: "action" },
];

function OnlineClaimTrigger(props) {
  useEffect(() => {
    console.clear();
    console.log(props.location.state);
  }, [props]);

  const config = require("../config.json");
  const testId = props.location.state.data.testId || "";
  const claims = props.location.state.data.claims || [];
  const env = props.location.state.data.env || "";
  const runID = props.location.state.data.runID || "";
  const claimFilters = props.location.state.data.claimFilters || [];
  const environments = config.environments;
  const history = useHistory();
  const [claimID, setClaimID] = useState("");
  
  const [currStep, setCurrStep] = useState(-1);
  const [alertMessage, setAlertMessage] = useState("");
  const [jenkinsBuild, setJenkinsBuild] = useState("");
  const [newClaimID, setNewClaimID] = useState("");
  const [triggerredOnce, setTriggerredOnce] = useState(false);

  const [jenkinsRuns, setJenkinsRuns] = useState(
    props.location.state.data.jenkinsRuns || []
  );

  // const [allEnv, setAllEnv] = useState(environments[0]);
  const[targetEnv,setTargetEnv] = useState(environments[0]);

  const envOptions = environments.map((env) => (
    <option key={uuid()} value={env}>
      {env}
    </option>
  ));

  const triggerOnlineClaim = () => {
    const data = {
      claimID,
      env,
      targetEnv,
      runID,
      subscriberID: null,
      providerID: null,
      overrides: [],
      claimFilters,
    };    
    
    setCurrStep(0);
    if (!triggerredOnce) {
      setTriggerredOnce(true);
    }
    axios
      .post(`${endpoint}/jenkins/create-claim-online/`, data)
      .then((res) => {
        setCurrStep((prevCurrStep) => prevCurrStep + 1);
        setJenkinsBuild(res.data);
        setCurrStep((prevCurrStep) => prevCurrStep + 1);
      })
      .catch((err) => {
        setAlertMessage(
          "Cannot retrieve build information, please check in jenkins manually"
        );
        console.log(err.response.data);
      });

    setCurrStep(2);
    // setJenkinsBuild(
    //     {
    //         buildID:38,
    //         buildURL:'https://devopslab.emblemhealth.com:8443/job/Automation/job/Facets/job/Caffe360/job/SIT2/job/ClaimCreationByClaimID/38',
    //         env,
    //         logs:'no logs'
    //     }
    // )
  };

  const reTriggerOnlineClaim = (runID, env, claimID) => {
    const data = {
      claimID,
      env,
      runID,
      subscriberID: null,
      providerID: null,
      overrides: [],
      claimFilters,
    };    
    
    setCurrStep(0);
    if (!triggerredOnce) {
      setTriggerredOnce(true);
    }
    axios
      .post(`${endpoint}/jenkins/create-claim-online/`, data)
      .then((res) => {
        setCurrStep((prevCurrStep) => prevCurrStep + 1);
        setJenkinsBuild(res.data);
        setCurrStep((prevCurrStep) => prevCurrStep + 1);
      })
      .catch((err) => {
        setAlertMessage(
          "Cannot retrieve build information, please check in jenkins manually"
        );
        console.log(err.response.data);
      });

    setCurrStep(2);
    // setJenkinsBuild(
    //     {
    //         buildID:38,
    //         buildURL:'https://devopslab.emblemhealth.com:8443/job/Automation/job/Facets/job/Caffe360/job/SIT2/job/ClaimCreationByClaimID/38',
    //         env,
    //         logs:'no logs'
    //     }
    // )
  };

  const refreshRun = (index) => {
    const data = jenkinsRuns[index];
    axios
      .post(`${endpoint}/jenkins/job/logs`, data)
      .then((res) => {
        const jenkinsRuns_clone = [...jenkinsRuns];
        jenkinsRuns_clone[index] = res.data;
        setJenkinsRuns(jenkinsRuns_clone);
      })
      .catch((err) => {
        if (err.response) {
          message.error(err.response.data.message);
        } else {
          message.error("Error connecting api server, Please contact admin");
        }
      });
  };

  const refreshLogs = (buildNumber, buildURL) => {
    axios
      .post(`${endpoint}/jenkins/job/logs`, {
        buildNumber, //:38,
        buildURL, //:'https://devopslab.emblemhealth.com:8443/job/Automation/job/Facets/job/Caffe360/job/SIT2/job/ClaimCreationByClaimID/38'
      })
      .then((res) => {
        setJenkinsBuild({ ...jenkinsBuild, logs: res.data.logs });
        setNewClaimID(res.data.createdDataID);
      })
      .catch((err) => {
        if (err.response) {
          message.error(err.response.data.message);
        } else {
          message.error("error connecting api server");
        }
      });
  };

  const validateClaim = (testId, env, claimID) => {
    history.push({
      pathname: "/validate/claims",
      state: { testId, env, claimID },
    });
  };

  return (
    <Layout>
      <PageHeader
        title="Online Claim Creation"
        subTitle="this will trigger corresponding jenkins job"
        onBack={() => history.push({ pathname: props.location.state.back })}
      />
      <Content className="online_claim_creation">
        <div className="claim_selection">
          
            <ComboBox
              label="claim ID"
              list={["", ...claims]}
              value={claimID}
              onChange={(e) => setClaimID(e.target.value)}
              disabled={triggerredOnce}
            />
            {/* Adding target environment */}
            <div className="execution-container__enviroment">
              <Space>
                <label htmlFor="environment__select" style={{color:"grey"}}>
                  ENVIRONMENT
                </label>
                <select
                  name="environment__select"
                  // value={allEnv}
                  // onChange={(e) => setAllEnv(e.target.value)}
                  value={targetEnv}
                  onChange={(e) => setTargetEnv(e.target.value)}
                >
                  {envOptions}
                </select>
              </Space>
            </div>
            <Button
              onClick={triggerOnlineClaim}
              type="primary"
              disabled={!claimID || (currStep > 0 && currStep < 3)}
            >
              {triggerredOnce ? "Re-Trigger Jenkins Job" : "Trigger Jenkins Job"}
            </Button>
          
        </div>

        {alertMessage && (
          <Alert
            message={alertMessage}
            style={{ margin: "10px 0px" }}
            closable
            onClose={setAlertMessage("")}
          />
        )}
        {currStep >= 0 && (
          <Steps current={currStep} className="steps-container">
            <Step
              title="Trigger"
              description="Triggers an online claim creation job in jenkins"
            />
            <Step title="Get Build" description="Check and get build info" />
            <Step title="Publish" description="Publish the build info" />
          </Steps>
        )}

        {jenkinsBuild && (
          <div className="current__build__info">
            <div className="info_card">
              <p>Build ID</p>
              <p>{jenkinsBuild.buildID}</p>
            </div>
            <div className="info_card">
              <p>URL</p>
              <p>
                <Button type="link" href={jenkinsBuild.buildURL}>
                  {jenkinsBuild.buildURL}
                </Button>
              </p>
              {!jenkinsBuild.logs && (
                <>
                  <Button
                    icon={<SyncOutlined />}
                    type="link"
                    onClick={() =>
                      refreshLogs(jenkinsBuild.buildID, jenkinsBuild.buildURL)
                    }
                  />
                  <Button
                    type="link"
                    onClick={() => validateClaim(testId, env, newClaimID)}
                    disabled={!newClaimID}
                  >
                    Validate
                  </Button>
                </>
              )}
            </div>

            {newClaimID && (
              <div className="info_card">
                <p>New Facets Claim ID</p>
                <p>{newClaimID}</p>
              </div>
            )}

            {jenkinsBuild.logs && (
              <>
                <Divider />

                <Collapse
                  accordion
                  style={{
                    marginBottom: 50,
                    padding: 5,
                    backgroundColor: "#fff",
                    maxHeight: 450,
                  }}
                >
                  <Panel
                    header="Logs"
                    className="jenkins_logs_box"
                    key={1}
                    style={{ maxHeight: 420, padding: 0 }}
                  >
                    <div
                      className="logs"
                      style={{
                        backgroundColor: "#fff",
                        color: "#555",
                        fontStyle: "italic",
                        fontSize: "10pt",
                        height: 350,
                        overflow: "auto",
                      }}
                    >
                      {jenkinsBuild.logs.split("\n").map((line, index) => (
                        <span key={`jenkins_service_line_${index}`}>
                          {line}
                          <br />
                        </span>
                      ))}
                    </div>
                  </Panel>
                </Collapse>
              </>
            )}
          </div>
        )}

        {jenkinsRuns && jenkinsRuns.length > 0 && (
          <>
            <Divider />
            <Table
              columns={columns}
              dataSource={jenkinsRuns.map((jr, index) => ({
                ...jr,
                key: `jenkins_run_${index}`,
                action: (
                  <>
                    {" "}
                    <Button
                      type="link"
                      icon={<SyncOutlined />}
                      onClick={() => refreshRun(index)}
                    />
                    <Button
                      type="link"
                      onClick={() =>
                        validateClaim(testId, env, jr.createdDataID)
                      }
                      disabled={!jr.createdDataID}
                    >
                      Validate
                    </Button>
                    <Button
                      type="link"
                      onClick={() =>
                        reTriggerOnlineClaim(runID, env, jr.inputDataID)
                      }
                      disabled={!jr.createdDataID}
                    >
                      Re-Trigger
                    </Button>

                    
                  </>
                ),
              }))}
              bordered
              small
              title={() => <span>Jenkins Run History</span>}
              style={{ margin: "30px 0px" }}
              expandedRowRender={(record) => (
                <p>
                  <span>Jenkins URL :</span>
                  <Button type="link" href={record.buildURL}>
                    {record.buildURL}
                  </Button>
                </p>
              )}
            />
          </>
        )}
      </Content>
    </Layout>
  );
}

export default OnlineClaimTrigger;
