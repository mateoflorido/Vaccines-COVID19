use mongodb::bson::doc;
use std::error::Error;
use std::vec::Vec;
use tokio;
use tokio::time::delay_for;
use std::time::Duration;
use rand::seq::SliceRandom;
use rand::Rng;

const ID: &str = "_id";
const STOCK: &str = "stock";

#[tokio::main]
async fn main() -> Result<(), Box<dyn Error>> {
    let client_uri = "mongodb://labIngress:test123@192.168.1.115:27017/lab";
    let client = mongodb::Client::with_uri_str( client_uri.as_ref( ) ).await?;
    let mut rng = rand::thread_rng( );
    let vaccs = [ "COV19VAC1", "COV19VAC2", "COV19VAC3" ];

    println!("Databases:");
    for name in client.list_database_names(None, None).await? {
        println!("- {}", name );
    }
    let vaccines = client.database( "lab" ).collection( "vaccines" );
    let mut rand_vac = vaccs.choose( &mut rng ).unwrap( );
    loop {
        rand_vac = vaccs.choose( &mut rng ).unwrap( );
        let vac_docs = vaccines
            .find_one(
                doc! {
                    "type": &rand_vac
                    },
                    None,
                ).await?
            .expect("Not Found :(");
        let _update_result = vaccines
            .update_one(
                doc! {
                    "_id": vac_docs.get_object_id( ID )?,
                     },
                doc! {
                    "$set": { "stock": vac_docs.get_i32( STOCK )? + rng.gen_range( 1, 5000 ) }
                    },
                    None,
                    ).await?;
        println!( "Shipped random container to {}", rand_vac );
        delay_for( Duration::from_millis( rng.gen_range( 5000, 7000 ) ) ).await;
        println!( "Shipping new stock...\n" );
    }
}
