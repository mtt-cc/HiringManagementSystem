import {BrowserRouter, Routes as ReactDomRoutes, Route, useNavigate} from 'react-router-dom';
import HomePage from "./Home.tsx";
import ProfilePage from "./Profile.tsx";
import ComponentTest from "./component.tsx";
import JobOffers from "./JobOffers.tsx";
import JobOffer from "./JobOffer.tsx";
import ContactsPage from "./Contacts.tsx";
import ContactPage from "./Contact.tsx";
import NewContactPage from "./NewContact.tsx";
import {NavigationRoute} from "../types/Navigation.ts";
import {NavigationContext} from "../Context.tsx";
import {PageContainer} from "../App.tsx";
import Dashbord from "./Dashbord.tsx";
import MessagesPage from "./MessagesPage.tsx";
import MessagePage from "./MessagePage.tsx";
import ManagerPage from "./ManagerPage.tsx";

function Routes() {
    const navigate = useNavigate();

    const routes = {
        home: {
            to: () => navigate("/ui"),
            path: "/ui",
            title: "Home Page",
            pageElement: <HomePage/>
        },
        dashboard: {
            to: () => navigate("/ui/dashboard"),
            path: "/ui/dashboard",
            title: "Dashboard",
            pageElement: <Dashbord/>
        },
        profile: {
            to: () => navigate("/ui/profile"),
            path: "/ui/profile",
            title: "User profile",
            pageElement: <ProfilePage/>
        },
        component: {
            to: () => navigate("/ui/component"),
            path: "/ui/component",
            title: "Component",
            pageElement: <ComponentTest/>
        },
        jobOffers: {
            to: () => navigate("/ui/job-offers"),
            path: "/ui/job-offers",
            title: "Job offers",
            pageElement: <JobOffers/>
        },
        jobOffer: {
            to: (id) => navigate(`/ui/job-offers/${id}`),
            path: "/ui/job-offers/:id",
            title: "Job offer",
            pageElement: <JobOffer/>
        },
        contacts: {
            to: () => navigate("/ui/contacts"),
            path: "/ui/contacts",
            title: "Contacts",
            pageElement: <ContactsPage/>
        },
        newContact: {
            to: () => navigate("/ui/contacts/new"),
            path: "/ui/contacts/new",
            title: "New Contact",
            pageElement: <NewContactPage/>
        },
        contact: {
            to: (id: number) => navigate(`/ui/contacts/${id}`),
            path: "/ui/contacts/:id",
            title: "Contact",
            pageElement: <ContactPage/>
        },
        messages: {
            to: () => navigate("/ui/messages"),
            path: "/ui/messages",
            title: "Messages",
            pageElement: <MessagesPage/>
        },
        message: {
            to: (state) => navigate(`/ui/messages/${state.state.message.id}`, { state }),
            path: "/ui/messages/:id",
            title: "Message",
            pageElement: <MessagePage/>
        },
        manager: {
            to: () => navigate("/ui/manager"),
            path: "/ui/manager",
            title: "Manager",
            pageElement: <ManagerPage />
        },
    } as Record<string, NavigationRoute>;

    return (
        <NavigationContext.Provider value={{
            navigate: navigate,
            routes: routes
        }}>
            <ReactDomRoutes>
                {
                    Object.values(routes).map((route, index) => (
                        <Route key={index} path={route.path} element={
                            <PageContainer title={route.title}>
                                {route.pageElement}
                            </PageContainer>
                        }/>
                    ))
                }
            </ReactDomRoutes>
        </NavigationContext.Provider>
    )
}

function Router() {
    return (
        <BrowserRouter>
            <Routes/>
        </BrowserRouter>
    );
}

export default Router;