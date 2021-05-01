$(function(){
    $(".heading-compose").click(function() {
        $(".side-two").css({
            "left": "0"
        });
    });

    $(".newMessage-back").click(function() {
        $(".side-two").css({
            "left": "-100%"
        });
    });

    let contactSearchBox = $("#composeText");
    let contactSearchAction = $("#search");
    contactSearchBox.keydown(function (e) {
        let key = e.which;
        if (key === 13) {
            contactSearchAction.click();
            return false;
        }
    });

    const getNewContactHtml = function (userId, firstname, lastname) {
        return `            <div id=${userId} class="row sideBar-body">
                            <div class="col-sm-3 col-xs-3 sideBar-avatar">
                                <div class="avatar-icon">
                                    <img src="https://bootdey.com/img/Content/avatar/avatar1.png">
                                </div>
                            </div>
                            <div class="col-sm-9 col-xs-9 sideBar-main">
                                <div class="row">
                                    <div class="col-sm-8 col-xs-8 sideBar-name">
                                        <span class="name-meta">${firstname} ${lastname}</span>
                                    </div>
                                    <div class="col-sm-4 col-xs-4 pull-right sideBar-time">
                                        <span class="text-primary pull-right">New!</span>
                                    </div>
                                </div>
                            </div>
                     </div>
        `;
    }

    const getContactNotFoundHtml = function (message) {
        return `            <div class="row sideBar-body">
                            <div class="col-sm-9 col-xs-9 sideBar-main">
                                <div class="row">
                                    <div class="col-sm-8 col-xs-8 sideBar-name">
                                        <span class="name-meta">${message}</span>
                                    </div>
                                </div>
                            </div>
                     </div>
        `;
    }

    contactSearchAction.click(function() {
        let term = contactSearchBox.val();
        console.log(term + " in click and clear all search result");

        $("#searchResult").empty();

        $.ajax({
            url: "/chat/search-friend?username="+term,
            type: 'get',
            success: function (data) {
                console.log(data);
                if (data.success) {
                    let userId = data.user.id;
                    let firstname = data.user.firstName;
                    let lastname = data.user.lastName;
                    $("#searchResult").prepend(getNewContactHtml(userId, firstname, lastname));
                } else {
                    $("#searchResult").prepend(getContactNotFoundHtml(data.message));
                }
            },
            error: function (error) {
                console.error(error)
                window.location.href = "/";
            }
        });
    });
});