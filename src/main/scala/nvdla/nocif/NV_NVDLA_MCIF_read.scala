package nvdla
import chisel3._
import chisel3.experimental._
import chisel3.util._

class NV_NVDLA_MCIF_read(implicit conf: nocifConfiguration) extends Module {
    val io = IO(new Bundle{
        //general clock
        val nvdla_core_clk      = Input(Clock())
        val nvdla_core_rstn     = Input(Bool())
        val pwrbus_ram_pd       = Input(UInt(32.W))
        val reg2dp_rd_os_cnt    = Input(UInt(8.W))

        val reg2dp_rd_weight_cdma_dat   = Input(UInt(8.W))
        val reg2dp_rd_weight_cdma_wt    = Input(UInt(8.W))
        val reg2dp_rd_weight_sdp        = Input(UInt(8.W))
        val reg2dp_rd_weight_sdp_b      = if(conf.NVDLA_SDP_BS_ENABLE)  Some(Input(UInt(8.W))) else None
        val reg2dp_rd_weight_sdp_n      = if(conf.NVDLA_SDP_BN_ENABLE)  Some(Input(UInt(8.W))) else None
        val reg2dp_rd_weight_sdp_e      = if(conf.NVDLA_SDP_EW_ENABLE)  Some(Input(UInt(8.W))) else None
        val reg2dp_rd_weight_pdp        = if(conf.NVDLA_PDP_ENABLE)     Some(Input(UInt(8.W))) else None
        val reg2dp_rd_weight_cdp        = if(conf.NVDLA_CDP_ENABLE)     Some(Input(UInt(8.W))) else None
        val reg2dp_rd_weight_rbk        = if(conf.NVDLA_RUBIK_ENABLE)   Some(Input(UInt(8.W))) else None
        val reg2dp_rd_weight_bdma       = if(conf.NVDLA_BDMA_ENABLE)    Some(Input(UInt(8.W))) else None

        // cdma_dat-0, cdma_wt-1, sdp-2, sdp_b-3, sdp_n-4, sdp_e-5, pdp-6, cdp-7
        val rdma2mcif_rd_cdt_lat_fifo_pop   = Input(Vec(conf.RDMA_NUM, Bool()))
        val rdma2mcif_rd_req_valid          = Input(Vec(conf.RDMA_NUM, Bool()))
        val rdma2mcif_rd_req_ready          = Output(Vec(conf.RDMA_NUM, Bool()))
        val rdma2mcif_rd_req_pd             = Input(Vec(conf.RDMA_NUM, UInt(conf.NVDLA_DMA_RD_REQ.W)))
        val mcif2rdma_rd_rsp_valid          = Output(Vec(conf.RDMA_NUM, Bool()))
        val mcif2rdma_rd_rsp_ready          = Input(Vec(conf.RDMA_NUM, Bool()))
        val mcif2rdma_rd_rsp_pd             = Output(Vec(conf.RDMA_NUM, UInt(conf.NVDLA_DMA_RD_RSP.W)))

        //noc2mcif
        val noc2mcif_axi_r_rvalid = Input(Bool())
        val noc2mcif_axi_r_rready = Output(Bool())
        val noc2mcif_axi_r_rid = Input(UInt(8.W))
        val noc2mcif_axi_r_rlast = Input(Bool())
        val noc2mcif_axi_r_rdata = Input(UInt(conf.NVDLA_PRIMARY_MEMIF_WIDTH.W))

        //mcif2noc
        val mcif2noc_axi_ar_arvalid = Output(Bool())
        val mcif2noc_axi_ar_arready = Input(Bool())
        val mcif2noc_axi_ar_arid = Output(UInt(8.W))
        val mcif2noc_axi_ar_arlen = Output(UInt(4.W))
        val mcif2noc_axi_ar_araddr = Output(UInt(conf.NVDLA_MEM_ADDRESS_WIDTH.W))
    })

    val eg2ig_axi_vld = Wire(Bool())

    val u_ig = Module(new NV_NVDLA_MCIF_READ_ig)
    u_ig.io.nvdla_core_clk      := io.nvdla_core_clk
    u_ig.io.nvdla_core_rstn     := io.nvdla_core_rstn
    u_ig.io.pwrbus_ram_pd       := io.pwrbus_ram_pd
    u_ig.io.reg2dp_rd_os_cnt    := io.reg2dp_rd_os_cnt

    u_ig.io.rdma2mcif_rd_cdt_lat_fifo_pop  := io.rdma2mcif_rd_cdt_lat_fifo_pop
    u_ig.io.rdma2mcif_rd_req_valid         := io.rdma2mcif_rd_req_valid
    io.rdma2mcif_rd_req_ready              :=  u_ig.io.rdma2mcif_rd_req_ready
    u_ig.io.rdma2mcif_rd_req_pd            := io.rdma2mcif_rd_req_pd

    // regs
    u_ig.io.reg2dp_rd_weight_cdma_dat   := io.reg2dp_rd_weight_cdma_dat
    u_ig.io.reg2dp_rd_weight_cdma_wt    := io.reg2dp_rd_weight_cdma_wt
    u_ig.io.reg2dp_rd_weight_sdp        := io.reg2dp_rd_weight_sdp
    if(conf.NVDLA_SDP_BS_ENABLE)    { u_ig.io.reg2dp_rd_weight_sdp_b.get  := io.reg2dp_rd_weight_sdp_b.get}
    if(conf.NVDLA_SDP_BN_ENABLE)    { u_ig.io.reg2dp_rd_weight_sdp_n.get  := io.reg2dp_rd_weight_sdp_n.get}
    if(conf.NVDLA_SDP_EW_ENABLE)    { u_ig.io.reg2dp_rd_weight_sdp_e.get  := io.reg2dp_rd_weight_sdp_e.get}
    if(conf.NVDLA_PDP_ENABLE)       { u_ig.io.reg2dp_rd_weight_pdp.get    := io.reg2dp_rd_weight_pdp.get}
    if(conf.NVDLA_CDP_ENABLE)       { u_ig.io.reg2dp_rd_weight_cdp.get    := io.reg2dp_rd_weight_cdp.get}
    if(conf.NVDLA_RUBIK_ENABLE)     { u_ig.io.reg2dp_rd_weight_rbk.get    := io.reg2dp_rd_weight_rbk.get}
    if(conf.NVDLA_BDMA_ENABLE)      { u_ig.io.reg2dp_rd_weight_bdma.get   := io.reg2dp_rd_weight_bdma.get}


    u_ig.io.eg2ig_axi_vld          := eg2ig_axi_vld
    io.mcif2noc_axi_ar_arvalid     := u_ig.io.mcif2noc_axi_ar_arvalid
    u_ig.io.mcif2noc_axi_ar_arready:= io.mcif2noc_axi_ar_arready
    io.mcif2noc_axi_ar_arid        := u_ig.io.mcif2noc_axi_ar_arid
    io.mcif2noc_axi_ar_arlen       := u_ig.io.mcif2noc_axi_ar_arlen
    io.mcif2noc_axi_ar_araddr      := u_ig.io.mcif2noc_axi_ar_araddr


    val u_eg = Module(new NV_NVDLA_MCIF_READ_eg)
    u_eg.io.nvdla_core_clk      := io.nvdla_core_clk
    u_eg.io.nvdla_core_rstn     := io.nvdla_core_rstn
    u_eg.io.pwrbus_ram_pd       := io.pwrbus_ram_pd
    eg2ig_axi_vld               := u_eg.io.eg2ig_axi_vld

    io.mcif2rdma_rd_rsp_valid       :=  u_eg.io.mcif2rdma_rd_rsp_valid
    u_eg.io.mcif2rdma_rd_rsp_ready  := io.mcif2rdma_rd_rsp_ready
    io.mcif2rdma_rd_rsp_pd          :=  u_eg.io.mcif2rdma_rd_rsp_pd

    u_eg.io.noc2mcif_axi_r_rvalid    := io.noc2mcif_axi_r_rvalid
    io.noc2mcif_axi_r_rready         := u_eg.io.noc2mcif_axi_r_rready
    u_eg.io.noc2mcif_axi_r_rid       := io.noc2mcif_axi_r_rid
    u_eg.io.noc2mcif_axi_r_rlast     := io.noc2mcif_axi_r_rlast
    u_eg.io.noc2mcif_axi_r_rdata     := io.noc2mcif_axi_r_rdata
}