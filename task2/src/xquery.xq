declare namespace saxon = "http://saxon.sf.net/";
declare option saxon:output "indent=yes";
(: Specify the XQuery here :)
<won>
    {
        let $users := auctions/users/user
        for $user in $users
            order by $user/@username ascending
            return <user name="{$user/@username}">
                {
                    let $bidProducts := /auctions/bids/product
                    for $bidProduct in $bidProducts
                        where $bidProduct/bid[last()]/@user=$user/@username
                        return <product value="{$bidProduct/bid[@user=$user/@username][last()]}">{/auctions/products/product[@id=$bidProduct/@id]/name/text()}</product>
                }
        </user>
    }
</won>