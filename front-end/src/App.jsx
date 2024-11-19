import React from "react";
import {
  HashRouter as Router,
  Switch,
  Route,
  Redirect,
} from "react-router-dom";
import {
  Navigation,
  Home,
  Manage,
  NotFound,
  Execute,
  JobMonitor,
  ViewJob,
  JobSubmission,
  AddOrUdpate,
  SideNav,
  ManageMapping,
  EDIEXtracts,
  OnlineClaimTrigger,
  Dashboard,
  ClaimValidationResult,
  CVQueueAndForm,
  ClaimAccuracyValidator,
  ClaimAccuracyConditions,
  ClaimAccuracyResult,
  CreateUser,
  Testcases,
  ClaimDetails,
  ClaimStatus,
  PlaceofService,
  ClaimDetailsTable
  
  
} from "./components";

import { Layout } from "antd";
import { UserProvider } from "./Contexts/UserContext";
import "./App.css";
import BulkLoad from "./components/BulkLoad";
import  ClaimsDashboardView from "./components/ClaimsDashboardView";

function App() {
  const { Header, Content } = Layout;

  return (
    <UserProvider>
      <div className="App">
        <Layout>
          <Router basename={process.env.REACT_APP_ROUTE_BASE}>
            <Header className="top-nav">
              <Navigation />
            </Header>
            <Layout>
              <SideNav />
              <Content className="main_content" id="main_content">
                <Switch>
                  <Route exact path="/" component={Home} />
                  <Route exact path="/design" component={Manage} />
                  <Route exact path="/execute" component={Execute} />
                  <Route exact path="/job/submit" component={JobSubmission} />
                  <Route exact path="/add" component={AddOrUdpate} />
                  <Route exact path="/update/:id" component={AddOrUdpate} />
                  <Route exact path="/jobs" component={JobMonitor} />
                  <Route exact path="/jobs/view/:id" component={ViewJob} />
                  <Route exact path="/createuser" component={CreateUser} />
                  <Route exact path="/testcases" component={Testcases} />
                  <Route exact path="/bulkload" component={BulkLoad} />
                  <Route exact path="/dashboardView1" component={ClaimsDashboardView} />
                  <Route exact path="/claimDetails1" component={ClaimDetails} />
                  <Route exact path="/claimStatus" component={ClaimStatus} />
                  <Route exact path="/placeofservice" component={PlaceofService} />
                  <Route exact path="/claimdetailstable" component={ClaimDetailsTable} />


                  <Route
                    exact
                    path="/config/mapping"
                    component={ManageMapping}
                  />
                  <Route exact path="/result/extract" component={EDIEXtracts} />
                  <Route
                    exact
                    path="/run/claim-online/:id"
                    component={OnlineClaimTrigger}
                  />
                  <Route exact path="/dashboard" component={Dashboard} />
                  <Route
                    exact
                    path="/validate/claims"
                    component={CVQueueAndForm}
                  />
                  <Route
                    exact
                    path="/validate/claims/:id"
                    component={ClaimValidationResult}
                  />
                  <Route exact path="/home">
                    <Redirect exact to="/"></Redirect>
                  </Route>
                  <Route
                    exact
                    path="/tools/claimaccuracyvalidator"
                    component={ClaimAccuracyValidator}
                  />
                  <Route
                    exact
                    path="/tools/claimaccuracyvalidator/conditions"
                    component={ClaimAccuracyConditions}
                  />
                   <Route
                    exact
                    path="/tools/claimaccuracyvalidator/:id"
                    component={ClaimAccuracyResult}

                   
                  
                  />
                  
                  <Route component={NotFound} />
                </Switch>
              </Content>
            </Layout>
          </Router>
        </Layout>
      </div>
    </UserProvider>
  );
}

export default App;
