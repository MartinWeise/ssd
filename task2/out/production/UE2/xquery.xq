declare namespace saxon = "http://saxon.sf.net/";
declare option saxon:output "indent=yes";
(: Specify the XQuery here :)
<won>
    {
        let $users := auctions/users/user
        for $user in $users
            return <user name="{$user/@username}">
                {
                    let $bidProducts := /auctions/bids/product
                    for $bidProduct in $bidProducts
                        let $bid := $bidProduct/bid[@user=$user/@username]
                        return
                            (: only select bids that match the current user :)
                            if (exists($bid)
                                (: & ended auctions :)
                                and exists(/auctions/products/product[@id=$bidProduct/@id]/expired)
                                (: & last bids :)
                                and $bid = /auctions/bids/product[@id=$bidProduct/@id]/bid[last()]
                            ) then <product value="{$bidProduct/bid[@user=$user/@username]}">{/auctions/products/product[@id=$bidProduct/@id]/name/text()}</product>
                            else ""
                }
        </user>
    }
</won>