import { AuthContext, NavigationContext } from "../Context.tsx";
import { useContext } from "react";

function Header(
    props: {
        pageTitle: string;
    }
) {
    const navigation = useContext(NavigationContext);
    const { user } = useContext(AuthContext);
    const { pageTitle } = props;

    const handleLinkClick = (e: React.MouseEvent, page: string) => {
        e.preventDefault();
        if (page === "Home") return navigation.routes.dashboard.to();
        if (page === "Job Offers") return navigation.routes.jobOffers.to();
        if (page === "Messages") return navigation.routes.messages.to();
        if (page === "Contacts") return navigation.routes.contacts.to();
        if (page === "Profile") return navigation.routes.profile.to();
        if (page === "Manager Dashboard") return navigation.routes.manager.to();
    };

    return (
        <header className="d-flex bg-success p-3 align-items-center">
            <img src="/ui/icon.png" alt="logo" />
            <h1 className="text-white ps-5">{pageTitle}</h1>

            {pageTitle === "Dashboard" ? (
                <div className="ms-auto d-flex">
                    <a href="/job-offers" onClick={(e) => handleLinkClick(e, "Job Offers")} className="custom-nav-btn ms-3">
                        Job Offers
                    </a>
                    <a href="/messages" onClick={(e) => handleLinkClick(e, "Messages")} className="custom-nav-btn ms-3">
                        Messages
                    </a>
                    <a href="/contacts" onClick={(e) => handleLinkClick(e, "Contacts")} className="custom-nav-btn ms-3">
                        Contacts
                    </a>
                    <a href="/profile" onClick={(e) => handleLinkClick(e, "Profile")} className="custom-nav-btn ms-3">
                        Profile
                    </a>
                </div>
            ) : pageTitle === "Manager" ? (
                <div className="ms-auto d-flex">
                    <a href="/job-offers" onClick={(e) => handleLinkClick(e, "Job Offers")}
                       className="custom-nav-btn ms-3">
                        Job Offers
                    </a>
                    <a href="/messages" onClick={(e) => handleLinkClick(e, "Messages")} className="custom-nav-btn ms-3">
                        Messages
                    </a>
                    <a href="/contacts" onClick={(e) => handleLinkClick(e, "Contacts")} className="custom-nav-btn ms-3">
                        Contacts
                    </a>
                    <a href="/profile" onClick={(e) => handleLinkClick(e, "Profile")} className="custom-nav-btn ms-3">
                        Profile
                    </a>
                </div>
            ) : (
                <div className="ms-auto d-flex">
                    {user && user.roles.includes("manager") ? (
                        <a href="/manager" onClick={(e) => handleLinkClick(e, "Manager Dashboard")} className="custom-nav-btn ms-3">
                            Manager Dashboard
                        </a>
                    ) : (
                        <a href="/" onClick={(e) => handleLinkClick(e, "Home")} className="custom-nav-btn ms-3">
                            Home
                        </a>
                    )}
                </div>
            )}
        </header>
    );
}

export default Header;