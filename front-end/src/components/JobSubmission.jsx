import React, { useEffect, useState, useContext } from "react";
import { Button, Result } from "antd";
import { Link, useHistory, Redirect } from "react-router-dom";
import { UserContext } from "../Contexts/UserContext";
import axios from "axios";

const endpoint = process.env.REACT_APP_API_URL;

function JobSubmission(props) {
  const history = useHistory();

  const { authUser } = useContext(UserContext);

  const [result, setResult] = useState({});

  useEffect(() => {
    if (authUser &&
      authUser.authenticated &&
      props.location.state &&
      props.location.state.selected) {
    
      const selected = props.location.state.selected;

      console.log(selected);

      axios
      .post(`${endpoint}/monitor/jobs/add`, selected)
      // .post(
      //   `${endpoint}/monitor/jobs/add`,
      //   selected.map((s) => ({
      //     testCondition: { ...s },
      //     recordLength: s.recordLength,
      //     env: s.test_env,
      //     status: "Submitted",
      //   }))
      // )

        .then((res) => {
          setResult({
            status: "success",
            title: `Job # ${res.data}`,
            subTitle: "Your Job Request Successfully Submitted",
            destination: "/jobs",
            buttonText: "View Jobs",
          });

          axios.post(`${endpoint}/monitor/jobs/run/${res.data}`);

          history.replace(history.location.pathname, null);
        })
        .catch((err) => {
          if (!err?.response) {
            setResult({
              status: "error",
              title: "Server Down",
              subTitle: "Please contact administrator",
            });
            return;
          }

          setResult({
            status: "500",
            title: "Internal Server Error",
            subTitle:
              "Sorry, your job request could not be submitted, try again",
            destination: "/execute",
            buttonText: "Back to Execute Page",
          });
        });
    }
  }, [props.location.state, authUser, history]);

  return (
    <>
      {authUser.authenticated && result ? (
        <Result
          status={result.status}
          title={result.title}
          subTitle={result.subTitle}
          extra={
            <Link to={{ pathname: result.destination }}>
              <Button type="ghost">{result.buttonText}</Button>
            </Link>
          }
        />
      ) : (
        <Redirect to={{ pathname: "/jobs" }} />
      )}
    </>
  );
}

export default JobSubmission;
