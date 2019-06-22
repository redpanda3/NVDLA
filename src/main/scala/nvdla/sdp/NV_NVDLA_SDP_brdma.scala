// package nvdla

// import chisel3._
// import chisel3.experimental._

// class NV_NVDLA_SDP_rdma extends Module {

// val io = IO(new Bundle {
//     //in clock
//     val nvdla_core_clk = Input(Clock())
//     val pwrbus_ram_pd = Input(UInt(32.W))

//     //cvif
//     val sdp_b2cvif_rd_req_valid = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
//     val sdp_b2cvif_rd_req_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
//     val sdp_b2cvif_rd_req_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))) else None
//     val cvif2sdp_b_rd_rsp_valid  = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(Bool())) else None
//     val cvif2sdp_b_rd_rsp_ready = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None
//     val cvif2sdp_b_rd_rsp_pd = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Input(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))) else None
//     val sdp_b2cvif_rd_cdt_lat_fifo_pop = if(conf.NVDLA_SECONDARY_MEMIF_ENABLE) Some(Output(Bool())) else None

//     //mcif
//     val sdp_b2mcif_rd_req_valid = Output(Bool())
//     val sdp_b2mcif_rd_req_ready = Input(Bool())
//     val sdp_b2mcif_rd_req_pd = Output(UInt(conf.NVDLA_CDMA_MEM_RD_REQ.W))
//     val mcif2sdp_b_rd_rsp_valid  = Input(Bool())
//     val mcif2sdp_b_rd_rsp_ready = Output(Bool())
//     val mcif2sdp_b_rd_rsp_pd = Input(UInt(conf.NVDLA_CDMA_MEM_RD_RSP.W))
//     val sdp_b2mcif_rd_cdt_lat_fifo_pop = Output(Bool())

//     val sdp_brdma2dp_alu_valid = Output(Bool()) 
//     val sdp_brdma2dp_alu_ready = Input(Bool())  
//     val sdp_brdma2dp_alu_pd = Output(UInt(conf.AM_DW2.W))

//     val sdp_brdma2dp_mul_valid = Output(Bool()) 
//     val sdp_brdma2dp_mul_ready = Input(Bool())  
//     val sdp_brdma2dp_mul_pd = Output(UInt(conf.AM_DW2.W))

//     val reg2dp_brdma_data_mode = Input(Bool())  
//     val reg2dp_brdma_data_size = Input(Bool())  
//     val reg2dp_brdma_data_use = Input(UInt(2.W))  
//     val reg2dp_brdma_ram_type = Input(Bool())  
//     val reg2dp_bs_base_addr_high = Input(UInt(32.W))  
//     val reg2dp_bs_base_addr_low = Input(UInt(32-conf.AM_DW.W))  
//     val reg2dp_bs_line_stride = Input(UInt(32-conf.AM_DW.W))   
//     val reg2dp_bs_surface_stride = Input(UInt(32-conf.AM_DW.W))  
//     val reg2dp_batch_number = Input(UInt(5.W))  
//     val reg2dp_channel = Input(UInt(13.W))  
//     val reg2dp_height = Input(UInt(13.W))  
//     val reg2dp_op_en = Input(Bool())  
//     val reg2dp_out_precision = Input(UInt(2.W)))  
//     val reg2dp_perf_dma_en = Input(Bool())  
//     val eg2dp_proc_precision = Input(UInt(2.W)))  
//     val reg2dp_width = Input(UInt(13.W))  
//     val reg2dp_winograd = Input(Bool())  
//     val dp2reg_brdma_stall = Output(UInt(32.W))   
//     val dp2reg_done = Output(Bool())  


//     val dla_clk_ovr_on_sync = Input(Clock())
//     val global_clk_ovr_on_sync = Input(Clock())
//     val tmc2slcg_disable_clock_gating = Input(Clock())
//     val brdma_slcg_op_en = Input(Bool())
//     val brdma_disable = Input(Bool())

// })

//     // Layer Switch
//     val layer_process = RegInit(false.B)
//     val eg_done = Wire(Bool())
//     val op_load = io.reg2dp_op_en & !layer_process
//     when(op_load){
//         layer_process := true.B
//     }
//     .elsewhen(eg_done){
//         layer_process := false.B
//     }

//     io.dp2reg_done := eg_done

//     //=======================================
//     val u_gate = Module(new NV_NVDLA_SDP_BRDMA_gate)
//     u_gate.io.nvdla_core_clk := io.nvdla_core_clk
//     u_gate.io.brdma_disable := io.brdma_disable
//     u_gate.io.brdma_slcg_op_en := io.brdma_slcg_op_en
//     u_gate.io.dla_clk_ovr_on_sync := io.dla_clk_ovr_on_sync
//     u_gate.io.global_clk_ovr_on_sync := io.global_clk_ovr_on_sync
//     u_gate.io.tmc2slcg_disable_clock_gating := io.tmc2slcg_disable_clock_gating
//     val nvdla_gated_clk = u_gate.io.nvdla_gated_clk 

//     val ig2cq_prdy = Wire(Bool())
//     val dma_rd_req_rdy = Wire(Bool())
//     val u_ig = Module(new NV_NVDLA_SDP_RDMA_ig)
//     u_ig.io.nvdla_core_clk := nvdla_gated_clk
//     u_ig.io.op_load := op_load
//     val ig2cq_pd = u_ig.io.ig2cq_pd
//     val ig2cq_pvld = u_ig.io.ig2cq_pvld
//     u_ig.io.ig2cq_prdy := ig2cq_prdy
//     val dma_rd_req_pd = u_ig.io.dma_rd_req_pd
//     val dma_rd_req_vld = u_ig.io.dma_rd_req_vld
//     u_ig.io.dma_rd_req_rdy := dma_rd_req_rdy
//     u_ig.io.reg2dp_op_en := io.reg2dp_op_en
//     u_ig.io.reg2dp_perf_dma_en := io.reg2dp_perf_dma_en
//     u_ig.io.reg2dp_winograd := io.reg2dp_winograd
//     u_ig.io.reg2dp_channel := io.reg2dp_channel
//     u_ig.io.reg2dp_height := io.reg2dp_height
//     u_ig.io.reg2dp_width := io.reg2dp_width
//     u_ig.io.reg2dp_proc_precision := io.reg2dp_proc_precision
//     u_ig.io.reg2dp_rdma_data_mode := io.reg2dp_rdma_data_mode
//     u_ig.io.reg2dp_rdma_data_size := io.reg2dp_rdma_data_size
//     u_ig.io.reg2dp_rdma_data_use := io.reg2dp_rdma_data_use
//     u_ig.io.reg2dp_base_addr_high := io.reg2dp_base_addr_high
//     u_ig.io.reg2dp_base_addr_low := io.reg2dp_base_addr_low
//     u_ig.io.reg2dp_line_stride := io.reg2dp_line_stride
//     u_ig.io.reg2dp_surface_stride := io.reg2dp_surface_stride
//     io.dp2reg_rdma_stall := u_ig.io.dp2reg_rdma_stall

//     val cq2eg_prdy = Wire(Bool())
//     val u_cq = Module(new NV_NVDLA_SDP_BRDMA_cq(conf.NVDLA_VMOD_SDP_BRDMA_LATENCY_FIFO_DEPTH, 16))
//     u_cq.io.nvdla_core_clk := nvdla_gated_clk
//     u_cq.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     ig2cq_prdy := u_cq.io.ig2cq_prdy
//     u_cq.io.ig2cq_pvld := ig2cq_pvld
//     u_cq.io.ig2cq_pd := ig2cq_pd
//     u_cq.io.cq2eg_prdy := cq2eg_prdy
//     val cq2eg_pvld = u_cq.io.cq2eg_pvld
//     val cq2eg_pd = u_cq.io.cq2eg_pd

//     val lat_fifo_rd_pd = Wire(UInt(conf.NVDLA_DMA_RD_RSP.W))
//     val lat_fifo_rd_pvld = Wire(Bool())
//     val sdp_rdma2dp_alu_ready = Wire(Bool())
//     val sdp_rdma2dp_mul_ready = Wire(Bool())
//     val u_eg = Module(new NV_NVDLA_SDP_RDMA_eg)
//     u_eg.io.nvdla_core_clk := nvdla_gated_clk
//     u_eg.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     u_eg.io.op_load := op_load 
//     eg_done := u_eg.io.eg_done
//     u_eg.io.cq2eg_pd := cq2eg_pd
//     u_eg.io.cq2eg_pvld := cq2eg_pvld
//     cq2eg_prdy := u_eg.io.cq2eg_prdy
//     val dma_rd_cdt_lat_fifo_pop = u_eg.io.dma_rd_cdt_lat_fifo_pop
//     u_eg.io.lat_fifo_rd_pd := lat_fifo_rd_pd
//     u_eg.io.lat_fifo_rd_pvld := lat_fifo_rd_pvld 
//     val lat_fifo_rd_prdy = u_eg.io.lat_fifo_rd_prdy
//     u_eg.io.sdp_rdma2dp_alu_ready := sdp_rdma2dp_alu_ready
//     u_eg.io.sdp_rdma2dp_mul_ready := sdp_rdma2dp_mul_ready
//     io.sdp_brdma2dp_alu_pd := u_eg.io.sdp_rdma2dp_alu_pd
//     io.sdp_brdma2dp_alu_valid := u_eg.io.sdp_rdma2dp_alu_valid
//     io.sdp_brdma2dp_mul_pd := u_eg.io.sdp_rdma2dp_mul_pd
//     io.sdp_brdma2dp_mul_valid := u_eg.io.sdp_rdma2dp_mul_valid
//     u_eg.io.reg2dp_batch_number := io.reg2dp_batch_number
//     u_eg.io.reg2dp_channel := io.reg2dp_channel
//     u_eg.io.reg2dp_height := io.reg2dp_height
//     u_eg.io.reg2dp_width := io.reg2dp_width
//     u_eg.io.reg2dp_proc_precision := io.reg2dp_proc_precision
//     u_eg.io.reg2dp_out_precision := io.reg2dp_out_precision
//     u_eg.io.reg2dp_rdma_data_mode := io.reg2dp_brdma_data_mode
//     u_eg.io.reg2dp_rdma_data_size := io.reg2dp_brdma_data_size
//     u_eg.io.reg2dp_rdma_data_use := io.reg2dp_brdma_data_use

//     val lat_wr_pvld = Wire(Bool())
//     val u_lat_fifo = Module(new NV_NVDLA_SDP_BRDMA_lat_fifo(conf.NVDLA_VMOD_SDP_BRDMA_LATENCY_FIFO_DEPTH, conf.NVDLA_DMA_RD_RSP))
//     u_lat_fifo.io.nvdla_core_clk := nvdla_gated_clk
//     u_lat_fifo.io.pwrbus_ram_pd := io.pwrbus_ram_pd
//     val lat_wr_prdy = u_lat_fifo.io.lat_wr_prdy
//     u_lat_fifo.io.lat_wr_pvld := lat_wr_pvld
//     u_lat_fifo.io.lat_wr_pd := lat_wr_pd
//     u_lat_fifo.io.lat_rd_prdy := lat_rd_prdy
//     val lat_rd_pvld = u_lat_fifo.io.lat_rd_pvld
//     val lat_rd_pd = u_lat_fifo.io.lat_rd_pd

//     u_NV_NVDLA_SDP_RDMA_dmaif = Module(new NV_NVDLA_SDP_RDMA_dmaif)
//     u_NV_NVDLA_SDP_RDMA_dmaif.io.nvdla_core_clk := nvdla_gated_clk
//     if(conf.NVDLA_SECONDARY_MEMIF_ENABLE){
//         io.sdp_b2cvif_rd_cdt_lat_fifo_pop.get := u_NV_NVDLA_SDP_RDMA_dmaif.io.sdp2cvif_rd_cdt_lat_fifo_pop.get
//         io.sdp2cvif_rd_req_pd.get := u_NV_NVDLA_SDP_RDMA_dmaif.io.sdp2cvif_rd_req_pd.get
//         io.sdp2cvif_rd_req_valid.get := u_NV_NVDLA_SDP_RDMA_dmaif.io.sdp2cvif_rd_req_valid.get
//         u_NV_NVDLA_SDP_RDMA_dmaif.io.sdp2cvif_rd_req_ready.get := sdp2cvif_rd_req_ready
//         u_NV_NVDLA_SDP_RDMA_dmaif.io.
//         u_NV_NVDLA_SDP_RDMA_dmaif.io.

//     }








    







// }
