STIMA
=====
Stima is many-to-many communication library which enables quick and easy development of network applications.


Basic concept
-------------
- Each region has many clusters.  And each cluster has many members.
- Each member has 2 server instances for high performance.  The one handles control packets, the other handles data packets.
- Each member connects all other members to send the data and detect the status of the members.


Supporting various packet transmissions
---------------------------------------
- Anycast
  - send the data packet to the selected member in the cluster using round-robin.
  
- Unicast
  - send the data packet to the specific member.

- Broadcast
  - send the data packet to all members in the cluster.


Adding members dynamically
--------------------------
1. New member sends the own property including its own information when the member is added to a cluster.
2. The previous members which are received the property compare to the own property.
3. The previous member updates the property if the property is different from its own property.


Removing members dynamically
----------------------------
- The other members detect and remove dynamically when the member shutdowns.


Supporting multi regions
------------------------
- Members send the data packet to the members in the same region.
- Members send the data packet to the members regardless of region when config.regionSeparation in Member.properties is false.
